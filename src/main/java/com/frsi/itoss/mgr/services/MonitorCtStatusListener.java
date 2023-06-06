package com.frsi.itoss.mgr.services;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.frsi.itoss.mgr.health.ManagerSelfMonitorHealthService;
import com.frsi.itoss.model.repository.MonitorCtStatusRepo;
import com.frsi.itoss.shared.ManagerAction;
import com.frsi.itoss.shared.MonitorCtKey;
import com.frsi.itoss.shared.MonitorCtStatus;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@Log
public class MonitorCtStatusListener {
    @Autowired
    ManagerSelfMonitorHealthService statisticService;

    @Autowired
    MonitorCtStatusRepo monitorCtStatusRepo;

    @Async("taskExecutor")
    @EventListener(condition = "#event.operation.name() == 'SAVE_MONITOR_CT_STATUS'")
    public void listenMonitorCtStatusEvent(ManagerAction event) {
        log.info(event.getOperation() + " " + event.getActionObject().toString());
        ObjectMapper mapper = new ObjectMapper();
        MonitorCtStatus monitorCtStatus = mapper.convertValue(event.getActionObject(), MonitorCtStatus.class);


        Long start = System.currentTimeMillis();
        this.statisticService.inc("received", "save_monitor_ct_status");
        this.statisticService.inc("processing", "save_monitor_ct_status");
        MonitorCtKey monitorCtKey = monitorCtStatus.getId();
        var optionalMonitorCtStatus = monitorCtStatusRepo.findById(monitorCtKey);
        MonitorCtStatus foundMonitorCtStatus;
        if (optionalMonitorCtStatus.isPresent()) {
            foundMonitorCtStatus = optionalMonitorCtStatus.get();
            if (foundMonitorCtStatus.getStatus() != monitorCtStatus.getStatus()) {
                foundMonitorCtStatus.setLastChange(new Date());
            }
        } else {
            foundMonitorCtStatus = new MonitorCtStatus();
            foundMonitorCtStatus.setId(monitorCtKey);
            foundMonitorCtStatus.setCreatedAt(new Date());
            foundMonitorCtStatus.setLastChange(foundMonitorCtStatus.getCreatedAt());
            foundMonitorCtStatus.setCollectorId(monitorCtStatus.getCollectorId());
        }
        foundMonitorCtStatus.setModifiedAt(new Date());
        foundMonitorCtStatus.setCollectorId(monitorCtStatus.getCollectorId());
        foundMonitorCtStatus.setStatus(monitorCtStatus.getStatus());
        foundMonitorCtStatus.setError(monitorCtStatus.getError());
        monitorCtStatusRepo.saveAndFlush(foundMonitorCtStatus);
        this.statisticService.dec("processing", "save_monitor_ct_status");
        this.statisticService.inc("finished", "save_monitor_ct_status");
        Long end = System.currentTimeMillis();
        Long duration = end - start;
        this.statisticService.setDuration("save_monitor_ct_status", duration);
    }


}
