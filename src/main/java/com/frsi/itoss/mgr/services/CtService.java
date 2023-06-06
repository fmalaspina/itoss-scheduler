package com.frsi.itoss.mgr.services;

import com.frsi.itoss.model.ct.Ct;
import com.frsi.itoss.model.projections.CtReportingProjection;
import com.frsi.itoss.model.repository.CtRepo;
import com.frsi.itoss.model.repository.CtStatusRepo;
import com.frsi.itoss.model.repository.DashboardEntryRepo;
import com.frsi.itoss.shared.CtStatus;
import com.frsi.itoss.shared.DashboardEntry;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

@Service
@Log
public class CtService {
    final int size = 16 * 1024 * 1024;
    final ExchangeStrategies strategies = ExchangeStrategies.builder()
            .codecs(codecs -> codecs.defaultCodecs().maxInMemorySize(size))
            .build();
    @Autowired
    DashboardEntryRepo dashboardEntryRepo;
    @Autowired
    CtRepo ctRepo;

    @Autowired
    CtStatusRepo ctStatusRepo;


    @Value("${itoss.collector.api.timeout:10000}")
    private int collectorApiTimeout;

    @Transactional
    public void reset(Long id) throws TimeoutException {
        Optional<Ct> optionalCt = ctRepo.findByIdPessimisticWrite(id);
        if (optionalCt.isPresent()) {
            String endpoint = optionalCt.get().getCollector().getEndpoint() + "/collectorApi";
            try {
                var webClient = WebClient.builder().exchangeStrategies(strategies).baseUrl(endpoint).build();

                //var success =
                        webClient.post().uri("/resetCt")
                        .body(Mono.just(id), Long.class)
                        .retrieve().bodyToMono(Boolean.class)
                        .retryWhen(Retry.fixedDelay(2, Duration.ofSeconds(1)))
                        .timeout(Duration.ofMillis(collectorApiTimeout)).block(Duration.ofMillis(collectorApiTimeout));
            } catch (Exception e) {
                throw new TimeoutException("Timeout trying to reset ct data on collector Error:" +e.getMessage());
            }
        }
        Optional<CtStatus> optionalCtStatus = ctStatusRepo.findByIdPessimisticWrite(id);
        if (optionalCtStatus.isPresent()) {
            CtStatus ctStatus = optionalCtStatus.get();
            ctStatusRepo.delete(ctStatus);
        }
        List<DashboardEntry> deList = dashboardEntryRepo.findByIdCtIdPessimisticWrite(id);
        dashboardEntryRepo.deleteAll(deList);
    }



    public List<CtReportingProjection> findByTypeIdAndStateAndCompanyIdAndMetricIdProjected(Long typeId, String state, Long companyId, Long metricId) {
        return this.ctRepo.findByTypeIdAndStateAndCompanyIdAndMetricIdProjected(typeId, state, companyId, metricId);
    }
    public List<Ct> findByTypeIdAndStateAndCompanyIdAndMetricId(Long typeId, String state, Long companyId, Long metricId) {
        return this.ctRepo.findByTypeIdAndStateAndCompanyIdAndMetricId(typeId, state, companyId, metricId);
    }

    public List<Ct> findByTypeIdAndStateAndLocationIdsAndMetricId(Long typeId, String state, List<Long> locationIds, Long metricId) {
        String strLocationIds = locationIds.stream().map(l -> l.toString()).collect(Collectors.joining(","));
        return this.ctRepo.findByTypeIdAndStateAndLocationIdsAndMetricId(typeId, state, strLocationIds, metricId);
    }

}
