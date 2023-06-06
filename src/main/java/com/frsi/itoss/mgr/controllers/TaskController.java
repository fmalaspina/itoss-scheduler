package com.frsi.itoss.mgr.controllers;

import com.frsi.itoss.model.collector.Collector;
import com.frsi.itoss.model.ct.Ct;
import com.frsi.itoss.model.repository.CollectorRepo;
import com.frsi.itoss.model.repository.CtRepo;
import com.frsi.itoss.model.repository.TaskRepo;
import com.frsi.itoss.model.task.Task;
import com.frsi.itoss.shared.TaskContext;
import com.frsi.itoss.shared.TaskResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import javax.validation.constraints.NotNull;
import java.time.Duration;
import java.util.Date;
import java.util.Optional;
import java.util.concurrent.*;

@RestController

public class TaskController {
    @Autowired
    CtRepo ctRepo;
    @Autowired
    TaskRepo taskRepo;
    @Autowired
    CollectorRepo collectorRepo;


    @Value("${itoss.collector.api.retries:3}")
    private int retries;


    @Value("${itoss.collector.api.retryDelay:2}")
    private int retryDelay;

    @Value("${itoss.collector.api.timeout:10000}")
    private int collectorApiTimeout;
    @Value("${itoss.manager.api.size:62914560}")
    private int size = 62914560;
    final ExchangeStrategies strategies = ExchangeStrategies.builder()
            .codecs(codecs -> codecs.defaultCodecs().maxInMemorySize(size))
            .build();

    @GetMapping(value = {"/runTask"}, produces = "application/json")
    public ResponseEntity<?> runTask(@RequestParam(required = true) Long taskId,
                                     @RequestParam(required = true) Long ctId,
                                     @RequestParam(required = false) Long collectorId) {
        Optional<Task> optionalTask = taskRepo.findById(taskId);
        final Task task;

        Optional<Ct> optionalCt = ctRepo.findById(ctId);
        final Ct ct;
        if (optionalCt.isPresent() /*&& optionalTask.isPresent()*/) {

            ct = optionalCt.get();
        } else {
            throw new RuntimeException("The ct was not found.");
        }
        if (optionalTask.isPresent()) {

            task = optionalTask.get();

        } else {
            throw new RuntimeException("The task was not found.");
        }

        String endpoint = getCollectorEndpoint(collectorId, ct);

        CompletableFuture<Object> cf = CompletableFuture.supplyAsync(() -> {
            try {
                return getTaskRawResponse(task, ct, endpoint, new Date());

            } catch (Exception e) {

                throw new CompletionException(e.getMessage(), e.getCause());

            }
        });
        Long timeout = (task.getTimeout() == null ? collectorApiTimeout : task.getTimeout());
        try {


            Object taskResult = cf.get(timeout, TimeUnit.MILLISECONDS);
            if (taskResult instanceof TaskResult) {
                return ResponseEntity.ok(taskResult);
            } else {
                return ResponseEntity.status(404).body(taskResult);
            }


        } catch (TimeoutException e) {
            return ResponseEntity.status(408)
                    .body(new TimeoutException("Tool timeout after:" + timeout + "ms."));

        } catch (InterruptedException e) {
            return ResponseEntity.status(404).body(e.getCause());
        } catch (ExecutionException e) {
            return ResponseEntity.status(404).body(e.getCause());
        } catch (Exception e) {
            return ResponseEntity.status(404).body(e.getCause());
        }
    }

    @NotNull
    public String getCollectorEndpoint(Long collectorId, Ct ct) {
        if (collectorId == null && ct.getCollector() == null)
            throw new RuntimeException("No collector assigned to Ct.");
        String endpoint;
        if (collectorId != null) {
            Optional<Collector> optionalCollector = collectorRepo.findById(collectorId);
            if (!optionalCollector.isPresent()) {
                throw new RuntimeException("The collector assigned to the task was not found.");
            }
            endpoint = optionalCollector.get().getEndpoint() + "/collectorApi";

        } else {
            Collector collector = ct.getCollector();
            if (collector.getEndpoint() == null) {
                throw new RuntimeException("Collector with no endpoint assigned.");
            }
            endpoint = collector.getEndpoint() + "/collectorApi";
        }
        return endpoint;
    }

    public TaskResult getTaskRawResponse(Task task, Ct ct, String endpoint, Date previousFireTime) throws Exception {


//        URL url;
//
//        Map<String, String> map = new HashMap<>();
//        try {
//            url = new URL(endpoint);
//        } catch (Exception e) {
//            throw new RuntimeException("Unable to build url for collector endpoint.");
//        }

//        JsonRpcHttpClient jsonRpcHttpClient = new JsonRpcHttpClient(url, map);
//        jsonRpcHttpClient.setConnectionTimeoutMillis(collectorApiTimeout);


        TaskContext tc = new TaskContext();
        tc.setTaskName(task.getName());
        tc.setTaskId(task.getId());
        tc.setPreviousFireTime(previousFireTime);
        tc.setInstrumentation(task.getInstrumentation().getName());
        tc.setInstrumentationParameters(task.getInstrumentation().getInstrumentationParameters());
        tc.setTimeout(task.getTimeout());
        tc.setTargetId(ct.getId());
        tc.setTargetInstrumentationParameterValues(ct.getInstrumentationParameterValues());
        tc.setTaskInstrumentationParameterValues(task.getInstrumentationParameterValues());

        try {
//            CollectorAPI collectorApi = ProxyUtil.createClientProxy(getClass().getClassLoader(), CollectorAPI.class,
//                    jsonRpcHttpClient);
//            TaskResult response = collectorApi.runTask(tc);
            var webClient = WebClient.builder().exchangeStrategies(strategies).baseUrl(endpoint).build();

            var response = webClient.post().uri("/runTask")
                    .body(Mono.just(tc), TaskContext.class)
                    .retrieve().bodyToMono(TaskResult.class)
                    .retryWhen(Retry.fixedDelay(retries, Duration.ofSeconds(retryDelay)))
                    .timeout(Duration.ofMillis(collectorApiTimeout)).block(Duration.ofMillis(collectorApiTimeout));
            return response;
        } catch (Exception e) {
            throw new RuntimeException("Connection with collector refused. Collector may be down.");
        }

    }
}





