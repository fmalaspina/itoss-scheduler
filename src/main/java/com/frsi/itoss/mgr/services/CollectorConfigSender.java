package com.frsi.itoss.mgr.services;


import com.frsi.itoss.model.parameters.Parameter;
import com.frsi.itoss.model.repository.CollectorRepo;
import com.frsi.itoss.model.repository.CtRepo;
import com.frsi.itoss.model.repository.ParametersRepo;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Optional;

@Log
@Service
@ConditionalOnProperty(
        value = "itoss.manager.role.primary",
        havingValue = "true"
)
public class CollectorConfigSender {

    @Autowired
    CollectorRepo collRepo;
    @Autowired
    CtRepo ctRepo;
    //    @Value("${itoss.scoringExpression:''}")
//    String scoringExpression;

    @Value("${itoss.manager.role.primary:false}")
    private boolean isPrimary;

    @Autowired
    ParametersRepo parameterRepo;

    private String scoring;

    @PostConstruct
    void setScoring() {
        Optional<Parameter> optionalScoring = parameterRepo.findById("SCORING");
        if (optionalScoring.isPresent() && !optionalScoring.get().getTextValue().strip().isEmpty()) {
            scoring = optionalScoring.get().getTextValue();
        } else {
            scoring = """
                                       
                    return 0.0;
                                      
                    """;
        }
    }

//    @Scheduled(cron = "${itoss.collector.configuration.cron:-}")
//    void send() {
//        if (isPrimary) {
//
//            collRepo.findAll().stream().filter(c -> c.getMode().equals(CollectorConfigurationMode.PUSH)).forEach(
//
//                    coll -> {
//                        try {
//                            CollectorConfiguration cc = new CollectorConfiguration();
//                            try {
//                                List<Ct> ctList = ctRepo.findByCollectorIdAndOperative(coll.getId());
//                                if (log.isLoggable(Level.INFO)) {
//                                    log.info("Cts sent:" + ctList.size() + " to collector " + coll.getName());
//                                }
//                                cc = coll.getConfiguration(ctList, Optional.empty(), scoring);
//                            } catch (Exception e) {
//                                // TODO Auto-generated catch block
//                                if (log.isLoggable(Level.SEVERE)) {
//                                    log.severe(e.getMessage());
//                                }
//                            }
//
//                            String endpoint = coll.getEndpoint() + "/collectorApi";
//                            URL url = null;
//                            // You can add authentication headers etc to this map
//                            Map<String, String> map = new HashMap<>();
//                            try {
//                                url = new URL(endpoint);
//                            } catch (Exception e) {
//                                if (log.isLoggable(Level.SEVERE)) {
//                                    log.severe(e.getMessage());
//                                }
//                            }
////                            JsonRpcHttpClient jsonRpcHttpClient = new JsonRpcHttpClient(url, map);
////
////                            CollectorAPI collectorApi = ProxyUtil.createClientProxy(getClass().getClassLoader(),
////                                    CollectorAPI.class, jsonRpcHttpClient);
//                            collectorApi.saveConfiguration(cc);
//                        } catch (Exception e) {
//                            if (log.isLoggable(Level.SEVERE)) {
//                                log.severe("Unable to send configuration to collector." + coll.getName());
//                            }
//                        }
//                    });
//
//        }
//    }
}
