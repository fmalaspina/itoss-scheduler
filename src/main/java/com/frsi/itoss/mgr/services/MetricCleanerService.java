package com.frsi.itoss.mgr.services;

import com.frsi.itoss.model.commonservices.TimeseriesDAOServices;
import com.frsi.itoss.model.repository.MetricRepo;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@ConditionalOnProperty(
        value = "itoss.manager.role.primary",
        havingValue = "true"
)
@Service
@Log
public class MetricCleanerService {


    @Autowired
    MetricRepo metricRepo;

    @Autowired
    TimeseriesDAOServices timeseriesDAO;

    @Transactional
    @Scheduled(cron = "${itoss.clean.metrics.cron:0 0 0 * * ?}")
    public void cleanMetrics() {

        metricRepo.findAll().forEach(m -> {
            try {
                if (m.getRetentionDays() != null) {
                    clean(m.getName().toLowerCase(), m.getRetentionDays());
                }
            } catch (Exception e) {
                log.severe(e.getMessage());
            }
        });

    }

    private void clean(String tableName, int days) throws Exception {
        timeseriesDAO.queryForList("SELECT to_regclass('" + tableName + "') as metric_name;"
                ).stream().filter(m -> String.valueOf(m.get("metric_name")).equals(tableName))
                .forEach(e -> {
                    try {
                        timeseriesDAO
                                .update("delete from " + e.get("metric_name") + " where time < now() - interval '" + days + " days'");
                    } catch (Exception ex) {
                        log.severe(ex.getMessage());
                    }
                });
    }
}