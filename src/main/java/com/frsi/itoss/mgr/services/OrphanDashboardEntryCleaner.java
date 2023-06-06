package com.frsi.itoss.mgr.services;

import com.frsi.itoss.model.repository.DashboardEntryRepo;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@ConditionalOnProperty(
        value = "itoss.manager.role.primary",
        havingValue = "true",
        matchIfMissing = false)
@Service
@Log
public class OrphanDashboardEntryCleaner {


    @Autowired
    DashboardEntryRepo dashboardEntryRepo;

    @Transactional
    @Scheduled(fixedDelayString = "${itoss.orphan.cleaner:120000}")
    public void clean() {
        log.info("Cleaning orphan dashboard entries.");
        dashboardEntryRepo.cleanOrphanDashboardEntries();

    }
}
