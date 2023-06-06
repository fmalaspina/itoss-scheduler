package com.frsi.itoss.mgr.services;

import com.frsi.itoss.model.repository.MonitorCtStatusRepo;
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
public class OrphanMonitorCtStatusCleaner {


    @Autowired
    MonitorCtStatusRepo monitorCtStatusRepo;

    @Transactional
    @Scheduled(fixedDelayString = "${itoss.orphan.cleaner:120000}")
    public void clean() {
        log.info("Cleaning orphan monitor Ct statuses.");
        monitorCtStatusRepo.cleanOrphanMonitorStatus();

    }
}
