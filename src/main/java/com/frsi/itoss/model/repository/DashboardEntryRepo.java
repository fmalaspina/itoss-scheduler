package com.frsi.itoss.model.repository;

import com.frsi.itoss.model.dashboard.DashboardEntryAndRelated;
import com.frsi.itoss.shared.DashboardEntry;
import com.frsi.itoss.shared.DashboardEntryKey;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.LockModeType;
import javax.persistence.QueryHint;
import java.util.List;
import java.util.Optional;


@Repository
public interface DashboardEntryRepo extends JpaRepository<DashboardEntry, DashboardEntryKey>, JpaSpecificationExecutor<DashboardEntry> {
    Optional<DashboardEntry> findById(DashboardEntryKey id);

    @Query(value = "WITH tennant AS (SELECT cts_id FROM tennant_cts tc INNER JOIN tennant_users tu ON tu.tennant_id = tc.tennant_id WHERE tu.users_id = ?1) SELECT * FROM dashboard_entry where container_id = ?2  and ct_id in "
            + "	(select cts_id from tennant)", nativeQuery = true)
    List<DashboardEntry> findByUserIdAndContainerId(Long userId, Long containerId);

    int deleteByIdCtId(Long ctId);

    Optional<DashboardEntry> findByIdCtIdAndIdMonitorIdAndIdObject(Long ctId, Long monitorId, String object);

    List<DashboardEntry> findByIdCtIdAndIdMonitorIdAndFault(Long ctId, Long monitorId, boolean fault);

    @Transactional
    @Modifying
    @Query(value = "delete from dashboard_entry where monitor_id = ?1 and ct_id = ?2 and fault = true", nativeQuery = true)
    int deleteByCtIdAndMonitorIdAndFaulted(Long monitorId, Long ctId);

    @Transactional
    @Modifying
    @Query(value = "delete from dashboard_entry where monitor_id = ?1 and ct_id = ?2 and fault = false", nativeQuery = true)
    int deleteByCtIdAndMonitorIdAndNonFaulted(Long monitorId, Long ctId);

    List<DashboardEntry> findByIdCtId(Long ctId);

    List<DashboardEntry> findByIdMonitorId(Long monitorId);

    List<DashboardEntry> findByIdObject(String object);

    @Query("select new com.frsi.itoss.model.dashboard.DashboardEntryAndRelated(c,m,d) from DashboardEntry d join Ct c on d.id.ctId = c.id join Monitor m on d.id.monitorId = m.id")
    List<DashboardEntryAndRelated> findAllRelated();

    List<DashboardEntry> findByIdCtIdAndIdMonitorId(Long ctId, Long monitorId);

    int deleteByIdMonitorId(Long monitorId);

    @Transactional
    @Modifying
    @Query(value = "delete from dashboard_entry where concat(ct_id, '_', monitor_id) not in (select concat(ct.id, '_', mpm.monitors_id) as key from ct inner join monitoring_profile_monitors mpm on ct.monitoring_profile_id = mpm.monitoring_profile_id)", nativeQuery = true)
    int cleanOrphanDashboardEntries();

    @Transactional
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @QueryHints({@QueryHint(name = "javax.persistence.lock.timeout", value = "3000")})
    @Query("SELECT d FROM DashboardEntry d WHERE d.id = ?1")
    Optional<DashboardEntry> findByIdPessimisticLocking(DashboardEntryKey key);

    @Transactional
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @QueryHints({@QueryHint(name = "javax.persistence.lock.timeout", value = "3000")})
    @Query("select d from DashboardEntry d where d.id.ctId = ?1")
    List<DashboardEntry> findByIdCtIdPessimisticWrite(Long id);
}
