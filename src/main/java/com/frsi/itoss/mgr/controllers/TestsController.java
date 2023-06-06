package com.frsi.itoss.mgr.controllers;

import com.frsi.itoss.model.collector.Collector;
import com.frsi.itoss.model.profile.Monitor;
import com.frsi.itoss.model.repository.CtRelationRepo;
import com.frsi.itoss.model.repository.CtRepo;
import com.frsi.itoss.model.repository.MonitorRepo;
import com.frsi.itoss.shared.CollectorConfiguration;
import com.frsi.itoss.shared.CollectorResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@RestController

@RequestMapping("/cts")
public class TestsController {

    @Autowired
    CtRepo ctRepo;
    @Autowired
    CtRelationRepo ctRelationRepo;
    @Autowired
    MonitorRepo monitorRepo;

    @Value("${itoss.scoringExpression:''}")
    String scoringExpression;


    @Value("${itoss.collector.api.retries:3}")
    private int retries;


    @Value("${itoss.collector.api.retryDelay:2}")
    private int retryDelay;

    @Value("${itoss.collector.api.timeout:10000}")
    private int collectorApiTimeout;
    @Value("${itoss.collector.api.size:62914560}")
    private int size = 62914560;

    final ExchangeStrategies strategies = ExchangeStrategies.builder()
            .codecs(codecs -> codecs.defaultCodecs().maxInMemorySize(size))
            .build();

    @GetMapping(value = {"/{id}/test", "/{id}/test/{monitorId}"}, produces = "application/json")

    public ResponseEntity<?> test(@PathVariable(required = true) Long id,
                                  @PathVariable(required = false) Optional<Long> monitorId) {

        CompletableFuture<ResponseEntity<?>> cf = CompletableFuture.supplyAsync(() -> {

            try {
                var ct = ctRepo.findByIdProjection(id);

                if (ct.isPresent()) {

                    var ctFinal = ct.get();
                    if (ctFinal.getCollector() == null)
                        throw new RuntimeException("Ct with no collector assigned.");
                    Collector collector = ctFinal.getCollector();
                    if (collector.getEndpoint() == null)
                        throw new RuntimeException("Collector with no endpoint assigned.");
                    String endpoint = collector.getEndpoint() + "/collectorApi";
//                    URL url = null;
//                    // You can add authentication headers etc to this map
//                    Map<String, String> map = new HashMap<>();
//                    try {
//                        url = new URL(endpoint);
//                    } catch (Exception e) {
//                        throw new RuntimeException("Unable to build url for collector endpoint.");
//                    }
//                    JsonRpcHttpClient jsonRpcHttpClient = new JsonRpcHttpClient(url, map);
//                    jsonRpcHttpClient.setConnectionTimeoutMillis(collectorApiTimeout);
                    CollectorConfiguration cc;
                    try {

                        Optional<Monitor> monitor = Optional.empty();
                        if (monitorId.isPresent())
                            monitor = monitorRepo.findById(monitorId.get());
                        cc = collector.getConfiguration(ctFinal, monitor, scoringExpression);
                        if (cc.getMonitors().size() == 0) {
                            throw new RuntimeException("The monitoring profiles assigned to the ct has no monitors.");
                        }
                    } catch (RuntimeException e) {
                        throw e;
                    } catch (Exception e) {
                        throw new RuntimeException("Unable to get configuration for Collector.");
                    }
                    try {
//                        CollectorAPI collectorApi = ProxyUtil.createClientProxy(getClass().getClassLoader(),
//                                CollectorAPI.class, jsonRpcHttpClient);


                        var webClient = WebClient.builder().exchangeStrategies(strategies).baseUrl(endpoint).build();

                        List<CollectorResponse> response = webClient.post().uri("/test")
                                .body(Mono.just(cc), CollectorConfiguration.class)
                                .retrieve().bodyToMono(ArrayList.class)
                                .retryWhen(Retry.fixedDelay(retries, Duration.ofSeconds(retryDelay)))
                                .timeout(Duration.ofMillis(collectorApiTimeout)).block(Duration.ofMillis(collectorApiTimeout));

                        //return ResponseEntity.ok(collectorApi.test(cc));
                        return ResponseEntity.ok(response);
                    } catch (Exception e) {
                        throw new RuntimeException("Connection with collector refused. Collector may be down.");
                    }

                } else {
                    throw new RuntimeException("The ct was not found.");
                }
            } catch (Exception e) {
                return ResponseEntity.status(404).body(e);
            }
        }).orTimeout(collectorApiTimeout, TimeUnit.MILLISECONDS);
        try {
            return cf.get(collectorApiTimeout, TimeUnit.MILLISECONDS);

        } catch (Exception e) {
            return ResponseEntity.status(408)
                    .body(new TimeoutException("No response from collector after:" + collectorApiTimeout + "ms." + e.getMessage()));
        }


    }
}
