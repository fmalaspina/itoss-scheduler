package com.frsi.itoss.mgr.controllers;

import com.frsi.itoss.model.collector.Collector;
import com.frsi.itoss.model.ct.Ct;
import com.frsi.itoss.model.repository.CtRepo;
import com.frsi.itoss.model.repository.ToolRepo;
import com.frsi.itoss.model.tool.Tool;
import com.frsi.itoss.shared.*;
import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Template;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import javax.validation.constraints.NotNull;
import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Service
public class ToolService {

    @Autowired
    CtRepo ctRepo;

    @Autowired
    ToolRepo toolRepo;

    @Value("${itoss.collector.api.timeout:10000}")
    private int collectorApiTimeout;

    @Value("${itoss.collector.api.retries:3}")
    private int retries;


    @Value("${itoss.collector.api.retryDelay:2}")
    private int retryDelay;
    @Value("${itoss.collector.api.size:62914560}")
    private int size = 62914560;

    final ExchangeStrategies strategies = ExchangeStrategies.builder()
            .codecs(codecs -> codecs.defaultCodecs().maxInMemorySize(size))
            .build();

    private CollectorResponse getToolRawResponse(Long toolId, Long ctId) {
        Optional<Ct> optionalCt = ctRepo.findById(ctId);
        Optional<Tool> optionalTool = toolRepo.findById(toolId);
        if (optionalCt.isPresent() && optionalTool.isPresent()) {
            final Ct ctFinal = optionalCt.get();
            final Tool tool = optionalTool.get();
            if (ctFinal.getCollector() == null)
                throw new RuntimeException("Ct with no collector assigned.");
            Collector collector = ctFinal.getCollector();
            if (collector.getEndpoint() == null)
                throw new RuntimeException("Collector with no endpoint assigned.");
            String endpoint = collector.getEndpoint() + "/collectorApi";



//            URL url;
//            // You can add authentication headers etc to this map
//            Map<String, String> map = new HashMap<>();
//            try {
//                url = new URL(endpoint);
//            } catch (Exception e) {
//                throw new RuntimeException("Unable to build url for collector endpoint.");
//            }

//            JsonRpcHttpClient jsonRpcHttpClient = new JsonRpcHttpClient(url, map);
//            jsonRpcHttpClient.setConnectionTimeoutMillis(collectorApiTimeout);
            CollectorConfiguration cc = this.getConfiguration(ctFinal, tool);
            try {
//                CollectorAPI collectorApi = ProxyUtil.createClientProxy(getClass().getClassLoader(), CollectorAPI.class,
//                        jsonRpcHttpClient);
//                CollectorResponse response = collectorApi.runTool(cc);
                var webClient = WebClient.builder().exchangeStrategies(strategies).baseUrl(endpoint).build();

                var response = webClient.post().uri("/runTool")
                        .body(Mono.just(cc), CollectorConfiguration.class)
                        .retrieve().bodyToMono(CollectorResponse.class)
                        .retryWhen(Retry.fixedDelay(retries, Duration.ofSeconds(retryDelay)))
                        .timeout(Duration.ofMillis(collectorApiTimeout)).block(Duration.ofMillis(collectorApiTimeout));

                Handlebars handlebars = new Handlebars();
                if (tool.getTemplate() != null) {
                    try {
                        Template template = handlebars.compileInline(tool.getTemplate());
                        response.setFormated(template.apply(response));
                    } catch (Exception e) {
                        throw new Exception("Unable to apply template to tool. Bad format.");
                    }
                }
                return response;
            } catch (Exception e) {
                throw new RuntimeException("Connection with collector refused. Collector may be down.");
            }

        } else {
            throw new RuntimeException("The ct or tool was not found.");
        }
    }

    private CollectorConfiguration getConfiguration(Ct ct, Tool m) {
        final CollectorConfiguration configuration = new CollectorConfiguration();
        MonitorConfiguration mc = new MonitorConfiguration();
        mc.setMonitorId(m.getId());
        mc.setMetricName(m.getMetric().getName());
        mc.setMetricId(m.getMetric().getId());
        mc.setMonitorName(m.getName());
        mc.setMetricCategory(m.getMetric().getMetricCategory());
        mc.setMetricPayloadAttributes(m.getMetric().getMetricPayloadAttributes());
        // mc.setStatusMetric(m.getMetric().isStatusMetric());
        InstrumentationAdapter ia = new InstrumentationAdapter();
        ia.setDescription(m.getMetric().getInstrumentation().getDescription());
        ia.setInstrumentationParameters(m.getMetric().getInstrumentation().getInstrumentationParameters());
        ia.setName(m.getMetric().getInstrumentation().getName());
        mc.setInstrumentationAdapter(ia);
        mc.setMonitorInstrumParamValues(m.getInstrumentationParameterValues());
        mc.setMetricInstrumParamValues(m.getMetric().getInstrumentationParameterValues());
        CtConfiguration ctc = new CtConfiguration();
        ctc.setCtId(ct.getId().toString());
        ctc.setCtAttributes(ct.getAttributes());
        ctc.setCtName(ct.getName());
        ctc.setCtType(ct.getType().getName());
        ctc.setTypeAttributes(ct.getType().getTypeAttributes());
        ctc.setEnvironment(ct.getEnvironment());
        ctc.setCtInstrumParamValues(ct.getInstrumentationParameterValues());
        if (ct.getWorkgroup() != null) {
            ctc.setWorkgroup(ct.getWorkgroup().getName());
        } else {
            ctc.setWorkgroup("<none>");
        }
        if (ct.getSupportUser() != null) {
            ctc.setSupportUser(ct.getSupportUser().getName());
        } else {
            ctc.setSupportUser("<none>");
        }
        if (ct.getCompany() != null) {
            ctc.setCompany(ct.getCompany().getName());
        } else {
            ctc.setCompany("<none>");
        }

        ctc.setProfileId(ct.getMonitoringProfile().getId().toString());
        ctc.setProfileName(ct.getMonitoringProfile().getName());
        ctc.setEnvironment(ct.getEnvironment());
        if (ct.getCompany() != null) {
            ctc.setCompany(ct.getCompany().getName());
        }
        mc.getCtConfiguration().add(ctc);
        configuration.getMonitors().add(mc);

        return configuration;
    }

    public String formatedToolResponse(Long toolId, Long ctId) {
        return getToolRawResponse(toolId, ctId).getFormated();
    }

    @NotNull
    public ResponseEntity<?> execute(Long toolId, Long ctId) {
        Optional<Tool> optionalTool = toolRepo.findById(toolId);
        final Tool tool;

        if (optionalTool.isPresent()) {

            tool = optionalTool.get();

        } else {
            throw new RuntimeException("The tool was not found.");
        }

        CompletableFuture<Object> cf = CompletableFuture.supplyAsync(() -> {
            try {

                return getToolRawResponse(toolId, ctId);

            } catch (Exception e) {

                throw new CompletionException(e.getMessage(), e.getCause());

            }
        });
        long timeout = (tool.getTimeout().isEmpty() ? collectorApiTimeout : Long.parseLong(tool.getTimeout().get().toString()));
        try {


            Object cr = cf.get(timeout, TimeUnit.MILLISECONDS);
            if (cr instanceof CollectorResponse) {
                return ResponseEntity.ok(cr);
            } else {
                return ResponseEntity.status(404).body(cr);
            }

//		} catch (CompletionException e) {
//
//			return ResponseEntity.status(404).body(e.getCause());

        } catch (TimeoutException e) {
            return ResponseEntity.status(408)
                    .body(new TimeoutException("Tool timeout after:" + timeout + "ms."));
//		} catch (RuntimeException e) {
//			return ResponseEntity.status(404).body(e);
//
//
        } catch (Exception e) {
            return ResponseEntity.status(404).body(e.getCause());
        }
    }
}
