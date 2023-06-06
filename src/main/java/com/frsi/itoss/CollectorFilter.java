package com.frsi.itoss;

import com.frsi.itoss.mgr.health.CollectorMetrics;
import com.frsi.itoss.mgr.health.CollectorsHealth;
import com.frsi.itoss.model.collector.Collector;
import com.frsi.itoss.model.repository.CollectorRepo;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
@Log

public class CollectorFilter extends OncePerRequestFilter {
    @Value("${itoss.collector.security.enabled:true}")
    boolean isSecurityEnabled;


    public List<CollectorsHealth> getCollectorHealthList() {
        return collectorHealthList;
    }

    List<CollectorsHealth> collectorHealthList = new ArrayList<>();
    private final CollectorRepo collectorRepo;
    public CollectorFilter(CollectorRepo collectorRepo) {
        this.collectorRepo = collectorRepo;
        this.collectorRepo.findAll().forEach(collector -> {
            var collectorName = collector.getName();
            var collectorMetrics = new CollectorMetrics(null,0L,null,0L);
            var collectorsHealth = new CollectorsHealth(collectorName,collectorMetrics);
            this.collectorHealthList.add(collectorsHealth);
        });
    }


    @SuppressWarnings("NullableProblems")
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        if (!isSecurityEnabled) {
            filterChain.doFilter(request,response);
            return;
        }
        boolean isAllowed = true;
        Collector collector = null;
        if (request.getRequestURL().toString().contains("managerApi")) {
            try {
                var collectorID = request.getHeader("x-collector-id");
                var remotePort = request.getHeader("x-collector-port");
                if (collectorID != null && !collectorID.isBlank()) {
                    var remoteIP = request.getRemoteAddr();

                    var collectorFound = collectorRepo.findById(Long.parseLong(collectorID));
                    if (collectorFound.isPresent()) {
                        collector = collectorFound.get();
                        var isSameEndpoint = collector.getEndpoint().equals("http://" + remoteIP + ":" + remotePort);
                        if (!isSameEndpoint) {
                            isAllowed = false;
                            log.severe("Collector ID:" + collectorID + " with address: " + remoteIP + ":" + remotePort + " was not allowed to communicte with manager. Remote IP does not match endpoint IP: " + collector.getEndpoint());
                        }
                    } else {
                        isAllowed = false;
                        log.severe("Collector ID:" + collectorID + " with address: " + remoteIP + ":" + remotePort + " was not allowed to communicte with manager.");

                    }

                    if (isAllowed) {
                        if (request.getRequestURL().toString().contains("getConfiguration")) {
                            updateCollectorStatsConfigRequest(collector);
                        } else {
                            updateCollectorStatsMessageReceived(collector);
                        }
                        filterChain.doFilter(request, response);
                    }
                } else {
                    isAllowed = false;
                }
            } catch (Exception ignore) {
                // ignore
            }
        }
        if (isAllowed) {
            filterChain.doFilter(request, response);
        } else {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        }
    }

    private void updateCollectorStatsConfigRequest(Collector collector) {
        // update collectorHealthList with new timestamp
        var collectorHealth = collectorHealthList.stream().filter(colHealth -> colHealth.getName().equals(collector.getName())).findFirst();
        if (collectorHealth.isPresent()) {
            var collectorMetrics = collectorHealth.get().getMetrics();
            collectorMetrics.setLast_config_request_timestamp(LocalDateTime.now());
            collectorMetrics.setLast_config_request_seconds(0L);
        }
    }

    private void updateCollectorStatsMessageReceived(Collector collector) {
       // update collectorHealthList with new timestamp
        var collectorHealth = collectorHealthList.stream().filter(colHealth -> colHealth.getName().equals(collector.getName())).findFirst();
        if (collectorHealth.isPresent()) {
            var collectorMetrics = collectorHealth.get().getMetrics();
            collectorMetrics.setLast_message_received_timestamp(LocalDateTime.now());
            collectorMetrics.setLast_message_received_seconds(0L);
        }
    }

}
