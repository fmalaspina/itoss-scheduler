package com.frsi.itoss.model.repository;

import com.frsi.itoss.model.dashboard.Dashboard;
import com.frsi.itoss.model.profile.EventRule;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface EventRuleRepo extends JpaRepository<EventRule, Long> {
    Page<Set<Dashboard>> findByNameContainingIgnoreCase(String name, Pageable page);

    @Query(value = "select * from event_rule e where e.monitor_id is null", nativeQuery = true)
    List<EventRule> findByMonitorIdIsNull();

    @Query(value = "select * from event_rule e where e.monitor_id = ?1 and e.ct_id = ?2", nativeQuery = true)
    List<EventRule> findByMonitorIdAndCtId(Long monitorId, Long ctId);

    @Query(value = "select * from event_rule e where e.monitor_id = ?1 and e.ct_id is null", nativeQuery = true)
    List<EventRule> findByMonitorIdAndCtIdIsNull(Long monitorId);

    @Query(value = "select * from event_rule e where e.monitor_id = ?1 and e.ct_id is null and e.phase = ?2", nativeQuery = true)
    List<EventRule> findByMonitorIdAndCtIdIsNullAndPhase(Long monitorId, String phase);

    @Query(value = "select * from event_rule e where e.monitor_id = ?1 and e.ct_id = ?2 and e.phase = ?3", nativeQuery = true)
    List<EventRule> findByMonitorIdAndCtIdAndPhase(Long monitorId, Long ctId, String phase);
}
