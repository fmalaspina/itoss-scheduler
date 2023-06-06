package com.frsi.itoss.mgr.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.frsi.itoss.mgr.health.ManagerSelfMonitorHealthService;
import com.frsi.itoss.model.repository.DashboardEntryRepo;
import com.frsi.itoss.shared.DashboardEntry;
import com.frsi.itoss.shared.DashboardEntryKey;
import com.frsi.itoss.shared.ManagerAction;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Date;
import java.util.Optional;
import java.util.logging.Level;

@Service
@Log
public class DashboardEntryEventListener {
    @Autowired
    ManagerSelfMonitorHealthService statisticService;

    @Autowired
    DashboardEntryRepo dashboardEntryRepo;

    @Async("taskExecutor")
    @EventListener(condition = "#event.operation.name() == 'SAVE_DASHBOARD_ENTRY'")
    public void listenDashboardEntrySaveEvent(ManagerAction event) {
        log.info(event.getOperation() + " " + event.getActionObject().toString());
        ObjectMapper mapper = new ObjectMapper();
        DashboardEntry dp = mapper.convertValue(event.getActionObject(), DashboardEntry.class);

        Long start = System.currentTimeMillis();
        this.statisticService.inc("received", "save_dashboardentries");
        this.statisticService.inc("processing", "save_dashboardentries");


        Optional<DashboardEntry> deFound = null;
        DashboardEntry de;
        Long monitorId = dp.getMetricPayloadData().getMonitorId();
        Long ctId = dp.getMetricPayloadData().getCtId();
        String object = dp.getMetricPayloadData().getTags().toString();
        DashboardEntryKey key = new DashboardEntryKey();
        key.setCtId(ctId);
        key.setMonitorId(monitorId);
        key.setObject(object);
        try {
            LockByKey.lock(key);
            deFound = dashboardEntryRepo.findById(key);
            if (deFound.isPresent()) {
                de = deFound.get();
                if (!de.getSeverity().equalsIgnoreCase(dp.getSeverity())) {
                    de.setCreatedAt(new Date());
                    de.setAttended(false);
                }
            } else {
                de = new DashboardEntry();
                de.setCreatedAt(new Date());
                de.setId(key);
            }

            de.setMetricPayloadData(dp.getMetricPayloadData());
            de.setRuleDescription(dp.getRuleDescription());
            de.setSeverity(dp.getSeverity());
            de.setContainerId(dp.getContainerId());
            de.setFault(dp.isFault());
            de.setLastChange(dp.getLastChange());
            de.setModifiedAt(new Date());
            de.setScore(dp.getScore());
            de.setCompanyId(dp.getCompanyId());
            de.setRuleId(dp.getRuleId());
            de = dashboardEntryRepo.saveAndFlush(de);


            log.info("Dashboard entry successfully persisted ctId:" + de.toString());
            Long end = System.currentTimeMillis();
            Long duration = end - start;
            this.statisticService.dec("processing", "save_dashboardentries");
            this.statisticService.inc("finished", "save_dashboardentries");
            this.statisticService.setDuration("save_dashboardentries", duration);

        } catch (Exception e) {
            this.statisticService.dec("processing", "save_dashboardentries");
            this.statisticService.inc("with_error", "save_dashboardentries");
            if (log.isLoggable(Level.SEVERE)) {
                StringWriter sw = new StringWriter();
                e.printStackTrace(new PrintWriter(sw));
                String exceptionAsString = sw.toString();
                log.severe("Unable to save Dashboard Entry:" + dp.toString() + " error:" + exceptionAsString);
            }
        } finally {
            LockByKey.unlock(key);
        }
    }

    @Async("taskExecutor")
    @EventListener(condition = "#event.operation.name() == 'REMOVE_DASHBOARD_ENTRY'")
    public void listenDashboardEntryRemoveEvent(ManagerAction event) {

        log.info(event.getOperation() + " " + event.getActionObject().toString());
        ObjectMapper mapper = new ObjectMapper();
        DashboardEntryKey dashboardEntryId = mapper.convertValue(event.getActionObject(), DashboardEntryKey.class);


        Long start = System.currentTimeMillis();
        this.statisticService.inc("received", "remove_dashboardentries");
        this.statisticService.inc("processing", "remove_dashboardentries");
        //Long ctId = dashboardEntryId.getCtId();
        //Long monitorId = dashboardEntryId.getMonitorId();

        try {
            Optional<DashboardEntry> deFound = null;
            DashboardEntry de;
            LockByKey.lock(dashboardEntryId);
            deFound = dashboardEntryRepo.findById(dashboardEntryId);
            if (deFound.isPresent()) {
                de = deFound.get();
                dashboardEntryRepo.delete(de);
                log.info("Dashboard entry deleted successfully dashboardEntryId:" + dashboardEntryId);
            }


            Long end = System.currentTimeMillis();
            Long duration = end - start;
            this.statisticService.dec("processing", "remove_dashboardentries");
            this.statisticService.inc("finished", "remove_dashboardentries");
            this.statisticService.setDuration("remove_dashboardentries", duration);
        } catch (Exception e) {
            if (log.isLoggable(Level.SEVERE)) {
                log.severe("Unable to delete Dashboard Entry:" + dashboardEntryId + " error:" + e.getMessage());
            }
            this.statisticService.dec("processing", "remove_dashboardentries");
            this.statisticService.inc("with_error", "remove_dashboardentries");

        } finally {
            LockByKey.unlock(dashboardEntryId);
        }
    }


}
