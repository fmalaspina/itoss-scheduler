package com.frsi.itoss.mgr.services;


import com.frsi.itoss.mgr.health.ManagerSelfMonitorHealthService;
import com.frsi.itoss.model.repository.CtRepo;
import com.frsi.itoss.shared.ManagerAction;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@Log
public class SaveMetricListener {
    @Autowired
    ManagerSelfMonitorHealthService statisticService;
    @Autowired
    ApplicationEventPublisher eventPublisher;

    @Autowired
    CtRepo ctRepo;

    @Async("taskExecutor")
    @EventListener(condition = "#event.operation.name() == 'SAVE_METRIC'")
    public void listenSaveMetricEvent(ManagerAction event) {

        log.info(event.getOperation() + " " + event.getActionObject().toString());
        //ObjectMapper mapper = new ObjectMapper();
        //MetricPayloadData metricPayloadData = mapper.convertValue(event.getActionObject(), MetricPayloadData.class);
        //Long start = System.currentTimeMillis();
        this.statisticService.inc("received", "save_metric");

        this.statisticService.inc("processing", "save_metric");


    }
}
