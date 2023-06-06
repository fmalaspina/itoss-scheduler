package com.frsi.itoss.mgr.services;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.frsi.itoss.mgr.health.ManagerSelfMonitorHealthService;
import com.frsi.itoss.model.ct.Ct;
import com.frsi.itoss.model.repository.CtRepo;
import com.frsi.itoss.model.repository.CtStatusRepo;
import com.frsi.itoss.model.statemachine.CtState;
import com.frsi.itoss.shared.CtStatus;
import com.frsi.itoss.shared.ManagerAction;
import com.frsi.itoss.shared.Notification;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;

@Service
@Log
public class CtStatusListener {
    @Autowired
    ManagerSelfMonitorHealthService statisticService;
    @Autowired
    ApplicationEventPublisher eventPublisher;
    @Autowired
    CtStatusRepo ctStatusRepo;
    @Autowired
    CtRepo ctRepo;

    @Async("taskExecutor")
    @EventListener(condition = "#event.operation.name() == 'SAVE_CT_STATUS'")
    public void listenCtStatusEvent(ManagerAction event) {

        log.info(event.getOperation() + " " + event.getActionObject().toString());
        ObjectMapper mapper = new ObjectMapper();
        CtStatus ctStatus = mapper.convertValue(event.getActionObject(), CtStatus.class);
        Long start = System.currentTimeMillis();
        this.statisticService.inc("received", "save_ct_status");

        this.statisticService.inc("processing", "save_ct_status");
        Long id = ctStatus.getId();
        try {
            LockByKey.lock(id);
            Optional<Ct> ct = ctRepo.findById(id);
            if (!ct.isPresent() || ct.get().getState() != CtState.OPERATIONS) {
                return;
            }

            CtStatus ctStatusFound = null;
            Optional<CtStatus> ctStatusOptional = null;
            List<String> tags = new ArrayList<>();

            ctStatusOptional = ctStatusRepo.findById(id);

            if (ctStatusOptional.isPresent()) {
                ctStatusFound = ctStatusOptional.get();

                if (ctStatusFound.isDown() != ctStatus.isDown()) {
                    tags.add("status_change");
                    if (ctStatus.isDown())
                        tags.add("status_down");
                    Notification notification = new Notification();
                    notification.setTitle("Changed status of "
                            + Optional.ofNullable(ct.get().getName()).orElse("ct not found") + " from "
                            + (ctStatusFound.isDown() ? "down" : "up") + " to " + (ctStatus.isDown() ? "down" : "up"));
                    notification.setType("status");
                    notification.getPayload().put("ctId", ct.get().getId().toString());
                    notification.setCtId(ct.get().getId());
                    notification.setTags(tags);
                    notification.setDestinations(null);
                    this.eventPublisher.publishEvent(notification);
                }
            } else {
                ctStatusFound = new CtStatus();
                tags.add("status_change");
                if (ctStatus.isDown())
                    tags.add("status_down");

                Notification notification = new Notification();
                notification
                        .setTitle("Changed status of " + Optional.ofNullable(ct.get().getName()).orElse("ct not found")
                                + " to " + (ctStatus.isDown() ? "down" : "up"));
                notification.setType("status");
                notification.getPayload().put("ctId", ct.get().getId().toString());
                notification.setCtId(ct.get().getId());
                notification.setTags(tags);
                notification.setDestinations(null);
                this.eventPublisher.publishEvent(notification);
            }

            ctStatusFound.setDown(ctStatus.isDown());
            //ctStatusFound.setLastStatusChange(
            //		(ctStatus.getLastStatusChange() == null) ? new Date() : ctStatus.getLastStatusChange());
            ctStatusFound.setLastStatusChange(ctStatus.getLastStatusChange());
            ctStatusFound.setId(id);
            ctStatusFound.setModifiedAt(new Date());
            ctStatusFound = ctStatusRepo.saveAndFlush(ctStatusFound);

            log.info("Ct status save successfully ctId:" + id);
            Long end = System.currentTimeMillis();
            Long duration = end - start;
            this.statisticService.dec("processing", "save_ct_status");
            this.statisticService.inc("finished", "save_ct_status");
            this.statisticService.setDuration("save_ct_status", duration);
        } catch (Exception e) {

            if (log.isLoggable(Level.SEVERE))
                log.severe("Unable to save CtStatus:" + ctStatus.toString() + " error:" + e.getMessage());
            this.statisticService.dec("processing", "save_ct_status");
            this.statisticService.inc("with_error", "save_ct_status");
        } finally {
            LockByKey.unlock(id);
        }


    }
}
