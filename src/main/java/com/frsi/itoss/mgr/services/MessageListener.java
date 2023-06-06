package com.frsi.itoss.mgr.services;


import com.frsi.itoss.mgr.health.ManagerSelfMonitorHealthService;
import com.frsi.itoss.model.repository.CtRepo;
import com.frsi.itoss.model.repository.DashboardEntryRepo;
import com.frsi.itoss.shared.*;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@Log
public class MessageListener {
    @Autowired
    ManagerSelfMonitorHealthService statisticService;
    @Autowired
    ApplicationEventPublisher eventPublisher;
    @Autowired
    DashboardEntryRepo dashboardEntryRepo;
    @Autowired
    CtRepo ctRepo;

    @Async("taskExecutor")
    @EventListener
    public void listenMessageEvent(ItossMessage message) {

        Long start = System.currentTimeMillis();
        this.statisticService.inc("received", "itoss_message");

        this.statisticService.inc("processing", "itoss_message");
        try {
            // Se comenta para que deje los DE cuando el monitor ct status cambia su estado. Siempre queda reflejada la ultima situación.
            // En frontend marcar con otro color cuando el monitor está en fault.

//            if (message.getStatus().getStatus() == MonitorStatus.FAULT) {
//                deleteNonFaultedDashboardEntries(message.getStatus().getId());
//            }
//            if (message.getStatus().getStatus() == MonitorStatus.OK) {
//                deleteFaultedDashboardEntries(message.getStatus().getId());
//            }
            ManagerAction mgrAction = new ManagerAction();
            mgrAction.setOperation(ItossOperation.SAVE_MONITOR_CT_STATUS);
            mgrAction.setActionObject(message.getStatus());
            this.eventPublisher.publishEvent(mgrAction);

            message.getActions().forEach(action -> {
                log.info("Publishing manager action " + action.toString());
                this.eventPublisher.publishEvent(action);
            });

            log.info("Ct status save successfully ct id and monitor Id: " + message.getStatus().getId().toString());
            Long end = System.currentTimeMillis();
            Long duration = end - start;
            this.statisticService.dec("processing", "itoss_message");
            this.statisticService.inc("finished", "itoss_message");
            this.statisticService.setDuration("itoss_message", duration);
        } catch (Exception e) {
            log.severe("Unable to save monitor ct status transaction, rolling back. " + e.getMessage());

            this.statisticService.dec("processing", "itoss_message");
            this.statisticService.inc("with_error", "itoss_message");

        }

    }

    @Async("taskExecutor")
    public void deleteFaultedDashboardEntries(MonitorCtKey monitorCtKey) {

        log.info("Cleaning faulted DEs." + monitorCtKey.toString());
        dashboardEntryRepo.deleteByCtIdAndMonitorIdAndFaulted(monitorCtKey.getMonitorId(), monitorCtKey.getCtId());

    }

    @Async("taskExecutor")
    public void deleteNonFaultedDashboardEntries(MonitorCtKey monitorCtKey) {
        log.info("Cleaning non faulted DEs." + monitorCtKey.toString());
        dashboardEntryRepo.deleteByCtIdAndMonitorIdAndNonFaulted(monitorCtKey.getMonitorId(), monitorCtKey.getCtId());

    }
}
