package com.frsi.itoss.model.repository;

import com.frsi.itoss.shared.MonitorCtKey;
import com.frsi.itoss.shared.MonitorCtStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;


@Repository
public interface MonitorCtStatusRepo extends JpaRepository<MonitorCtStatus, MonitorCtKey>, JpaSpecificationExecutor<MonitorCtKey> {
    Optional<MonitorCtStatus> findById(MonitorCtKey id);


    @Transactional
    @Modifying
    @Query(value = "delete from monitor_ct_status where concat(ct_id, '_', monitor_id) not in (select concat(ct.id, '_', mpm.monitors_id) as key from ct inner join monitoring_profile_monitors mpm on ct.monitoring_profile_id = mpm.monitoring_profile_id)", nativeQuery = true)
    int cleanOrphanMonitorStatus();
}
