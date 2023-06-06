package com.frsi.itoss.model.repository;

import com.frsi.itoss.model.profile.Monitor;
import com.frsi.itoss.shared.MetricCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Repository
@RepositoryRestResource
public interface MonitorRepo extends JpaRepository<Monitor, Long>, JpaSpecificationExecutor<Monitor> {
    Page<List<Monitor>> findByNameContainingIgnoreCase(String name, Pageable page);



    Page<List<Monitor>> findByMetricCtTypeId(Long ctTypeId, Pageable page);

    @Query(
            value = """
                    SELECT DISTINCT m.* 
                      FROM monitor m inner join metric me on m.metric_id = me.id 
                     WHERE (:ctTypeId = '' OR me.ct_type_id IN ( select id from cttypes(string_to_array(:ctTypeId, ',')::::int[]) ))
                     	   AND (:name = '' OR m.name ILIKE Concat('%',:name,'%')) 
                    """, countQuery = """
                    SELECT count(DISTINCT m.*) 
                      FROM monitor m inner join metric me on m.metric_id = me.id 
                     WHERE (:ctTypeId = '' OR me.ct_type_id IN ( select id from cttypes(string_to_array(:ctTypeId, ',')::::int[]) ))
                     	   AND (:name = '' OR m.name ILIKE Concat('%',:name,'%')) 
                    """,
            nativeQuery = true
    )
    Page<List<Monitor>> findByNameContainingIgnoreCaseAndMetricCtTypeId(@Param("name") String name, @Param("ctTypeId") String ctTypeId,Pageable page);

    @Query(value = "select m.* from monitor m  inner join monitoring_profile_monitors mpm on mpm.monitors_id = m.id and mpm.monitoring_profile_id = ?1", nativeQuery = true)
    Set<Monitor> findByMonitoringProfileId(Long id);

    List<Monitor> findByMetricMetricCategory(MetricCategory category);

    @Modifying
    @Transactional
    @Query(value = """
            begin\\;
            delete from dashboard_entry where monitor_id = :id\\;
            delete from dashboard_containers where containers_id IN (select container_id from monitor where id = :id)\\;
            delete from container where id IN (select container_id from monitor where id = :id)\\;
            delete from event_rule where monitor_id = :id\\;
            delete from monitoring_profile_monitors where monitors_id = :id\\;
            			
            commit\\;
            end\\;
            			
            """, nativeQuery = true)
    void deleteByMonitorId(@Param("id") Long id);

}
