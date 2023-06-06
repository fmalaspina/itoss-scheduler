package com.frsi.itoss.mgr.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.frsi.itoss.mgr.health.ManagerSelfMonitorHealthService;
import com.frsi.itoss.model.collector.Collector;
import com.frsi.itoss.model.parameters.Parameter;
import com.frsi.itoss.model.repository.CollectorRepo;
import com.frsi.itoss.model.repository.CtRepo;
import com.frsi.itoss.model.repository.DashboardEntryRepo;
import com.frsi.itoss.model.repository.ParametersRepo;
import com.frsi.itoss.model.statemachine.CtState;
import com.frsi.itoss.shared.*;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@RestController
@Service

@Log
public class ManagerAPIImpl implements ManagerAPI {

    @Autowired
    CtRepo ctRepo;

    @Autowired
    CollectorRepo collRepo;

    @Autowired
    ApplicationEventPublisher eventPublisher;


    @Autowired
    DashboardEntryRepo dashboardEntryRepo;
    @Autowired
    ParametersRepo parameterRepo;

    @Autowired
    ManagerSelfMonitorHealthService statisticService;

    private String scoring;

    @PostConstruct

    public void reloadScoring() {
        Optional<Parameter> optionalScoring = parameterRepo.findById("SCORING");
        if (optionalScoring.isPresent() && optionalScoring.get().getTextValue() != null && !optionalScoring.get().getTextValue().strip().isEmpty()) {
            scoring = optionalScoring.get().getTextValue();
        } else {
            scoring = """
                                       
                    return 0.0;
                                      
                    """;
        }

    }

    @RequestMapping(value = "/managerApi/getConfiguration", method = RequestMethod.GET)
    @Override
    public CollectorConfiguration getConfiguration(Long collectorId) {

        CollectorConfiguration cc;// = new CollectorConfiguration();
        Optional<Collector> collector = collRepo.findById(collectorId);
        if (collector.isPresent()) {
            try {

                //List<Ct> ctList = ctRepo.findByCollectorIdAndOperative(collector.get().getId());
                //var ctList = ctRepo.findByCollectorIdAndOperative(collector.get().getId());
                var ctList = ctRepo.findByCollectorIdAndState(collector.get().getId(),CtState.OPERATIONS);
                log.info("Cts sent:" + ctList.size() + " to collector " + collector.get().getName());
                cc = collector.get().getConfiguration(ctList, Optional.empty(), scoring);


            } catch (Exception e) {
                log.severe("An error occurred while getting configuration for collector " + collector.get().getName() + " with id " + collector.get().getId());
                return null;
            }

            return cc;
        } else {
            log.severe("An unknown collector Id is trying to get configuration. ID:" + collectorId);
            return null;
        }
    }

    @RequestMapping(value = "/managerApi/getCts", method = RequestMethod.GET)

    @Override
    public Set<CtConfiguration> getCts(@RequestParam("collectorId") Long collectorId) {
        CollectorConfiguration collectorConfiguration;
        Set<CtConfiguration> ctConfigurationSet = new HashSet<>();
        try {

            Optional<Collector> collector = collRepo.findById(collectorId);
            if (collector.isPresent()) {
                try {
                   // var ctList = ctRepo.findByCollectorIdAndOperative(collector.get().getId());
                    var ctList = ctRepo.findByCollectorIdAndState(collector.get().getId(),CtState.OPERATIONS);
                    log.info("Cts sent:" + ctList.size() + " to collector " + collector.get().getName());
                    collectorConfiguration = collector.get().getConfiguration(ctList, Optional.empty(), scoring);

                    collectorConfiguration.getMonitors().forEach(m -> {

                        ctConfigurationSet.addAll(m.getCtConfiguration());
                    });
                } catch (Exception e) {
                    return null;
                }

            }

        } catch (Exception e) {
            return null;
        }
        //var c = ctConfigurationSet.stream().filter(ct -> ct.getCtId().equals("203883"));
        //c.forEach(temp -> System.out.println(temp.toString()));

        return ctConfigurationSet;
    }

    @RequestMapping(value = "/managerApi/getMonitors", method = RequestMethod.GET)
    //@SerializeAllExcept({"ctConfiguration"})
    @Override
    public Set<MonitorConfiguration> getMonitors(@RequestParam("collectorId") Long collectorId) throws
            JsonProcessingException {


        CollectorConfiguration collectorConfiguration;
        Set<MonitorConfiguration> monitorConfigurationSet = new HashSet<>();
        try {

            Optional<Collector> collector = collRepo.findById(collectorId);
            if (collector.isPresent()) {
                try {
                    //var ctList = ctRepo.findByCollectorIdAndOperative(collector.get().getId());
                    var ctList = ctRepo.findByCollectorIdAndState(collector.get().getId(),CtState.OPERATIONS);
                    log.info("Cts sent:" + ctList.size() + " to collector " + collector.get().getName());
                    collectorConfiguration = collector.get().getConfiguration(ctList, Optional.empty(), scoring);
                    monitorConfigurationSet = collectorConfiguration.getMonitors();

                } catch (Exception e) {
                    return null;
                }

            }

        } catch (Exception e) {
            return null;
        }

        return monitorConfigurationSet;
    }


    @Override
    public boolean ping() {

        return true;
    }

    @PostMapping(value = "/managerApi/sendMessage", produces = "application/json")
    @Override

    public ApiResponse sendMessage(@RequestBody(required = false) ItossMessage message) {
        if (message == null) {
            return new ApiResponse(true);
        }

        try {
            this.eventPublisher.publishEvent(message);
            return new ApiResponse(true);
        } catch (Exception e) {
            log.severe("Unable to process itoss message sent from collector, rolling back. " + e.getMessage());
            return new ApiResponse(false);
        }
    }

    @Override
    @Deprecated
    public boolean saveDashboardEntry(DashboardEntryPayload dp) {
        this.eventPublisher.publishEvent(dp);
        return true;

    }

    @Override
    @Deprecated
    public boolean deleteDashboardEntry(DashboardEntryKey dashboardEntryId) {
        this.eventPublisher.publishEvent(dashboardEntryId);
        return true;

    }


    @Override
    @Deprecated
    public boolean sendMail(String[] destinations, String message, MetricPayloadData metricPayloadData, Long toolId) {
        Mail mail = new Mail(destinations, message, metricPayloadData, toolId);
        this.eventPublisher.publishEvent(mail);
        return true;
    }

    @Override
    @Deprecated
    public boolean sendNotification(Notification notification) {
        this.eventPublisher.publishEvent(notification);
        return true;
    }


    @Override
    @Deprecated
    public boolean persistDashboardEntry(DashboardEntryPayload dashboardEntryPayload) {
        this.eventPublisher.publishEvent(new CollectorEvent<DashboardEntryPayload>(dashboardEntryPayload, "saveDashboardEntry"));
        return true;
    }

    @Override
    @Deprecated
    public boolean cleanFaultedDashboardEntries(Set<MonitorCtKey> monitorCtKeys) {
        this.eventPublisher.publishEvent(new CollectorEvent<Set<MonitorCtKey>>(monitorCtKeys, "cleanFaultedDashboardEntries"));
        return true;
    }

    @Override
    @Deprecated
    public boolean cleanNonFaultedDashboardEntries(Set<MonitorCtKey> monitorCtKeys) {
        this.eventPublisher.publishEvent(new CollectorEvent<Set<MonitorCtKey>>(monitorCtKeys, "cleanNonFaultedDashboardEntries"));
        return true;
    }

    @Override
    @Deprecated
    public boolean removeDashboardEntry(DashboardEntryKey dashboardEntryKey) {
        this.eventPublisher.publishEvent(new CollectorEvent<DashboardEntryKey>(dashboardEntryKey, "removeDashboardEntry"));
        return true;
    }

    @Override
    @Deprecated
    public boolean setMonitorCtStatus(Set<MonitorCtStatus> monitorCtStatusSet) {
        this.eventPublisher.publishEvent(new CollectorEvent<Set<MonitorCtStatus>>(monitorCtStatusSet, "setMonitorCtStatus"));

        return false;
    }


    @Override
    @Deprecated
    public boolean saveCtStatus(CtStatus ctStatus) {

        this.eventPublisher.publishEvent(ctStatus);

        return true;
    }
}
