package com.frsi.itoss.mgr.services;

import com.frsi.itoss.model.repository.NewsRepo;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service

@Log
@ConditionalOnProperty(
        value = "itoss.manager.role.primary",
        havingValue = "true"
)
public class NotificationCleaner {


    @Autowired
    NewsRepo newsRepo;


    @Scheduled(cron = "${itoss.manager.notification-cleaner.cron:0 0 * * * ?}")
    private void deletOldNews() {
        newsRepo.deleteOldNews();

    }


}