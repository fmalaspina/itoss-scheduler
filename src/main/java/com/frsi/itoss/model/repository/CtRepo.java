package com.frsi.itoss.model.repository;

import com.frsi.itoss.model.ct.Ct;
import com.frsi.itoss.model.ct.CtProjection;
import com.frsi.itoss.model.projections.CtReportingProjection;
import com.frsi.itoss.model.statemachine.CtState;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.history.RevisionRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.LockModeType;
import javax.persistence.QueryHint;
import java.util.List;
import java.util.Optional;
import java.util.Set;


@Repository
@RepositoryRestResource

public interface CtRepo extends JpaRepository<Ct, Long>, JpaSpecificationExecutor<Ct> {
    @Query(value = """
    select count(*)as qty, state from ct where company_id = :companyId group by state;
""",
            nativeQuery = true)
    List<CtCountedByState> countByStateForCompany(Long companyId);

    @Query(value = """
    WITH tennant AS (SELECT cts_id FROM tennant_cts tc INNER JOIN tennant_users tu ON tu.tennant_id = tc.tennant_id WHERE tu.users_id = :userId
    ) select count(*)as qty, state from ct where (ct.id IN (SELECT cts_id FROM tennant)) group by state;
""",nativeQuery = true)
    List<CtCountedByState> countByStateForUserId(Long userId);
    Page<Set<Ct>> findByNameContainingIgnoreCase(String name, Pageable page);
    Page<Set<Ct>> findByNameContainingIgnoreCaseAndTypeId(String name, Long typeId,Pageable page);

    Page<Set<Ct>> findByNameContainingAndEnvironmentContainingAndId(String name, String environment, Long id, Pageable page);

    Page<Set<Ct>> findByKeyContainingIgnoreCase(String key, Pageable page);

    Page<Set<Ct>> findByLocationId(Long id, Pageable page);

    Page<Set<Ct>> findByWorkgroupId(Long id, Pageable page);

    Page<Set<Ct>> findByMonitoringProfileId(Long id, Pageable page);

    Ct findByName(String name);

    Page<Set<Ct>> findByWorkgroupIdIsIn(Set<Long> workgroups, Pageable page);

    Page<Set<Ct>> findByContactId(Long id, Pageable page);



    Page<List<Ct>> findByCollectorId(Long id, Pageable page);

    @RestResource(path = "findByCollectorIdNoPaged", rel = "findByCollectorIdNoPaged")
    @Query(value = "select * from ct where collector_id = ?1", nativeQuery = true)
    List<Ct> findByCollectorId(Long id);

//    @Query(value = "select * from ct left join ct_status on ct.id = ct_status.id where collector_id = ?1 and state = 'OPERATIONS'", nativeQuery = true)
//    List<Ct> findByCollectorIdAndOperative(Long id);

    @RestResource(path = "findByCustomEventRule", rel = "findByCustomEventRule")
    @Query(value = "SELECT DISTINCT ct.* FROM ct INNER JOIN event_rule ev ON ct.id = ev.ct_id  WHERE monitor_id = ?1 AND (?2 = '' OR ct.name LIKE '%?2%')", nativeQuery = true)
    Page<Set<Ct>> findByCustomEventRule(Long monitorId, String name, Pageable page);


    Page<Set<Ct>> findBySupportUserNameContainingIgnoreCase(String name, Pageable page);

    Page<Set<Ct>> findByWorkgroupNameContainingIgnoreCase(String name, Pageable page);

    Page<Set<Ct>> findByEnvironment(@Param("name") String name, Pageable pageeable);

    //Page<List<Ct>> findByTypeTypePath(@Param("name") String name, Pageable pageeable);
    Optional<Ct> findById(Long id);
    @Query("select ct from Ct ct where ct.id = ?1")
    Optional<CtProjection> findByIdProjection(Long id);

    @Query(value = "WITH RECURSIVE managertree AS (  SELECT  id, attributes, creation_date, last_modified_date, created_by, last_modified_by, parent_id, type_id, workgroup_manager_id   FROM workgroup   WHERE workgroup_manager_id = ?1 OR id in (SELECT workgroup_id FROM workgroup_user_accounts WHERE user_accounts_id = ?1 )    UNION ALL   SELECT e.id, e.attributes, e.creation_date, e.last_modified_date, e.created_by, e.last_modified_by, e.parent_id, e.type_id, e.workgroup_manager_id   FROM workgroup e         INNER JOIN managertree mtree ON mtree.id = e.parent_id ) SELECT * FROM ct where workgroup_id in (select id from managertree)", nativeQuery = true)
    Page<Set<Ct>> findByUserWorkgroups(Long id, Pageable page);

    @Query(value = "SELECT ct.* "
            + "			FROM ct INNER JOIN tennant_cts tc ON tc.cts_id = ct.id "
            + "			WHERE tc.tennant_id = ?1", countQuery = "SELECT count(ct.*) "
            + "			FROM ct INNER JOIN tennant_cts tc ON tc.cts_id = ct.id "
            + "			WHERE tc.tennant_id = ?1", nativeQuery = true)
    Page<Set<Ct>> findByTennantId(Long id, Pageable page);


    @Query(value = """
                 select * from ct where (?1 = '' OR name ILIKE Concat('%',?1,'%')) 
                 and (?2 = 0 OR type_id = ?2) 
                 and (?3 = '' OR environment = ?3) 
                 and (?4 = '' OR state = ?4) 
                 and (?5 = 0 OR location_id = ?5) 
                 and (?6 = 0 OR company_id = ?6) 
                 and (?7 = 0 OR workgroup_id = ?7)
                 and (?8 = 0 OR ?8 is null OR collector_id = ?8)
                 and (?9 = 0 OR ?9 is null OR monitoring_profile_id = ?9)
                 """, countQuery = """
    select count(*) from ct where (?1 = '' OR name ILIKE Concat('%',?1,'%')) 
                 and (?2 = 0 OR type_id = ?2) 
                 and (?3 = '' OR environment = ?3) 
                 and (?4 = '' OR state = ?4) 
                 and (?5 = 0 OR location_id = ?5) 
                 and (?6 = 0 OR company_id = ?6) 
                 and (?7 = 0 OR workgroup_id = ?7)
                 and (?8 = 0 OR ?8 is null OR collector_id = ?8)
                 and (?9 = 0 OR ?9 is null OR monitoring_profile_id = ?9)
    """,nativeQuery = true)
    Page<Set<Ct>> findBySpecification(String name, Long typeId, String environment, String state, Long locationId, Long companyId, Long workgroupId, Long collectorId, Long monitorProfileId, Pageable page);


    @Query(value =
            """
                    WITH tennant AS (SELECT cts_id 
                    FROM tennant_cts tc INNER JOIN tennant_users tu ON tu.tennant_id = tc.tennant_id 
                    WHERE tu.users_id = ?1) select ct.* 
                    from ct left join ct_status s on ct.id = s.id where (?2 = '' OR name LIKE Concat('%',?2,'%')) 
                    and (?3 = 0 OR type_id = ?3) 
                    and (?4 = '' OR environment = ?4) 
                    and (?5 = '' OR state = ?5) 
                    and (?6 = 0 OR location_id = ?6) 
                    and (?7 = 0 OR company_id = ?7) 
                    and (?8 = 0 OR workgroup_id = ?8) 
                    AND (?9 = '' OR (ct.id = ANY (select unnest(string_to_array(?9, ','))\\:\\:bigint)))    
                    AND ct.id in (select cts_id from tennant)
                    and (?10 = '' OR s.down = ?10\\:\\:boolean)
                    ORDER BY s.down DESC, CASE WHEN(environment = 'PRODUCTION') THEN 0 ELSE 4 END ASC
                    			
                    		""",
            nativeQuery = true, countQuery =
            """
                    WITH tennant AS (SELECT cts_id 
                    FROM tennant_cts tc INNER JOIN tennant_users tu ON tu.tennant_id = tc.tennant_id 
                    WHERE tu.users_id = ?1) select count(ct.*) 
                    from ct left join ct_status s on ct.id = s.id where (?2 = '' OR name LIKE Concat('%',?2,'%')) 
                    and (?3 = 0 OR type_id = ?3)	
                    and (?4 = '' OR environment = ?4) 
                    and (?5 = '' OR state = ?5) 
                    and (?6 = 0 OR location_id = ?6)	 
                    and (?7 = 0 OR company_id = ?7) 
                    and (?8 = 0 OR workgroup_id = ?8) 
                    AND (?9 = '' OR (ct.id = ANY (select unnest(string_to_array(?9, ','))\\:\\:bigint)))  
                    AND ct.id in (select cts_id from tennant)
                    and (?10 = '' OR s.down = ?10\\:\\:boolean)
                    			
                    		
                    """
    )
    Page<Set<Ct>> findByUser(Long userId, String name, Long typeId, String environment, String state, Long locationId, Long companyId, Long workgroupId, String ctIds, String down, Pageable page);

    List<Ct> findByCompanyName(String company);

    List<Ct> findByCompanyId(Long id);

    @Transactional
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @QueryHints({@QueryHint(name = "javax.persistence.lock.timeout", value = "3000")})
    @Query(value = "select c from Ct c where c.id = ?1")
    Optional<Ct> findByIdPessimisticWrite(Long id);

    Page<Set<Ct>> findByIdIn(List<Long> ids, Pageable page);



    List<CtProjection> findByCollectorIdAndState(Long id, CtState state);

    @Query(
            value = """
                    SELECT DISTINCT ct.* 
                      FROM ct 
                      	   LEFT JOIN monitoring_profile_monitors mpm ON mpm.monitoring_profile_id = ct.monitoring_profile_id
                    	   LEFT JOIN monitor mo ON mo.id = mpm.monitors_id
                    	   LEFT JOIN metric me ON me.id = mo.metric_id
                     WHERE (:typeId = 0 OR ct.type_id = :typeId)
                    	   AND (:state = '' OR ct.state = :state)
                    	   AND (:companyId = 0 OR ct.company_id = :companyId)
                    	   AND (:metricId = 0 OR me.id = :metricId)
                    """,
            nativeQuery = true
    )
    List<Ct> findByTypeIdAndStateAndCompanyIdAndMetricId(@Param("typeId") Long typeId, @Param("state") String state, @Param("companyId") Long companyId, @Param("metricId") Long metricId);

    @Query(
            value = """
                SELECT DISTINCT ct
                      FROM Ct ct
                      join  ct.monitoringProfile mp
                        join  mp.monitors m
                    	   WHERE (:typeId = 0L OR ct.type.id = :typeId)
                    	   AND (:state = '' OR ct.state = :state)
                    	   AND (:companyId = 0L OR ct.company.id = :companyId)
                    	   AND (:metricId = 0L OR m.metric.id = :metricId)
"""



    )
    List<CtReportingProjection> findByTypeIdAndStateAndCompanyIdAndMetricIdProjected(@Param("typeId") Long typeId, @Param("state") String state, @Param("companyId") Long companyId, @Param("metricId") Long metricId);

    @Query(
            value = """
                    SELECT DISTINCT ct.* 
                      FROM ct 
                      	   LEFT JOIN monitoring_profile_monitors mpm ON mpm.monitoring_profile_id = ct.monitoring_profile_id
                    	   LEFT JOIN monitor mo ON mo.id = mpm.monitors_id
                    	   LEFT JOIN metric me ON me.id = mo.metric_id
                     WHERE (:typeId = 0 OR ct.type_id = :typeId)
                    	   AND (:state = '' OR ct.state = :state)
                    	   AND (:locationIds = '' OR ct.location_id IN ( select id from locations(string_to_array(:locationIds, ',')::::int[]) ))
                    	   AND (:metricId = 0 OR me.id = :metricId)
                    """,
            nativeQuery = true
    )
    List<Ct> findByTypeIdAndStateAndLocationIdsAndMetricId(@Param("typeId") Long typeId, @Param("state") String state, @Param("locationIds") String locationIds, @Param("metricId") Long metricId);






}

