package com.frsi.itoss.model.repository;

import com.frsi.itoss.model.projections.DashboardEntriesAttendedProjection;
import com.frsi.itoss.model.projections.DashboardEntriesProjection;
import com.frsi.itoss.shared.DashboardEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

@RestController
public interface DashboardEntriesRepository extends JpaRepository<DashboardEntry, Long> {
    @Query(value = """
            SELECT
                   COALESCE(case when ct.environment = 'PRODUCTION' then 'PRODUCTION' else 'NON PRODUCTION' END, 'ALL') AS environment,
                   COALESCE(metric.metric_category, 'ALL') AS metricCategory,
                   COALESCE(t.name, 'ALL') AS ctType,
                   COALESCE(t.type_path, 'ALL') AS ctTypePath,
                   de.severity,
                   COUNT(*) AS total
               FROM public.dashboard_entry de
               INNER JOIN public.monitor m ON de.monitor_id = m.id
               INNER JOIN public.ct ct ON de.ct_id = ct.id
               INNER JOIN public.metric metric ON m.metric_id = metric.id
               INNER join public.ct_type t on ct.type_id = t.id
               where de.company_id = :companyId or :companyId = 0
               GROUP BY GROUPING SETS (
                   (de.severity),
                   (case when ct.environment = 'PRODUCTION' then 'PRODUCTION' else 'NON PRODUCTION' END, de.severity),
                   (metric.metric_category, de.severity),
                   (t.name,t.type_path, de.severity),
                   (t.name,t.type_path, case when ct.environment = 'PRODUCTION' then 'PRODUCTION' else 'NON PRODUCTION' END, de.severity),
                   (t.name,t.type_path, metric.metric_category, case when ct.environment = 'PRODUCTION' then 'PRODUCTION' else 'NON PRODUCTION' END, de.severity)
               )
               ORDER BY environment, metricCategory, ctType,severity                                                                                                                                
              """, nativeQuery = true)
    List<DashboardEntriesProjection> findDashboardEntriesByEnvironment(@Param("companyId") Long companyId);


@Query(value = """
           WITH tennant AS (SELECT cts_id FROM tennant_cts tc INNER JOIN tennant_users tu ON tu.tennant_id = tc.tennant_id WHERE tu.users_id = :userId
                             ) SELECT
                            
                             COALESCE(case when ct.environment = 'PRODUCTION' then 'PRODUCTION' else 'NON PRODUCTION' END, 'ALL') AS environment,
                             COALESCE(metric.metric_category, 'ALL') AS metricCategory,
                             COALESCE(t.id, 0) AS ctTypeId,
                             COALESCE(t.name, 'ALL') AS ctType,
                             COALESCE(t.type_path, 'ALL') AS ctTypePath,
                             CASE
                              WHEN de.score >= :highest THEN 'HIGHEST'
                                     WHEN de.score >= :high and de.score < :highest THEN 'HIGH'
                                     WHEN de.score >= :medium and de.score < :high THEN 'MEDIUM'
                                     WHEN de.score >= :low and de.score < :medium THEN 'LOW'
                                     ELSE 'LOWEST'
                             END AS score,
                             COUNT(*) AS total
                             FROM public.dashboard_entry de
                             INNER JOIN public.monitor m ON de.monitor_id = m.id
                             INNER JOIN public.ct ct ON de.ct_id = ct.id
                             INNER JOIN public.metric metric ON m.metric_id = metric.id
                             INNER JOIN public.ct_type t ON ct.type_id = t.id
                             WHERE (ct.id IN (SELECT cts_id FROM tennant)) AND ct.state = 'OPERATIONS'
                             GROUP BY GROUPING SETS (
                             (
                             score
                             ),
                             (
                                     case when ct.environment = 'PRODUCTION' then 'PRODUCTION' else 'NON PRODUCTION' END,
                             score
                             ),
                                     (
                             metric.metric_category,
                             score
                             ),
                                     (
                             t.id, t.name, t.type_path,
                             score
                             ),
                                     (
                             t.id, t.name, t.type_path,
                                     case when ct.environment = 'PRODUCTION' then 'PRODUCTION' else 'NON PRODUCTION' END,
                             score
                             ),
                                     (
                             t.id, t.name, t.type_path, metric.metric_category,
                                     case when ct.environment = 'PRODUCTION' then 'PRODUCTION' else 'NON PRODUCTION' END,
                             score
                             )
                                     )
                             ORDER BY
                             environment,
                             metricCategory,
                             ctType,
                             score
        """,nativeQuery = true)
    List<DashboardEntriesProjection> findDashboardEntriesByScore(@Param("userId") Long userId, @Param("low") int low,
                                                                 @Param("medium") int medium, @Param("high") int high,
                                                                 @Param("highest") int highest);



    @Query(value = """
    WITH tennant AS (SELECT cts_id FROM tennant_cts tc INNER JOIN tennant_users tu ON tu.tennant_id = tc.tennant_id WHERE tu.users_id = :userId
    ) SELECT
    "severity"->>'value' as "severity",
    COALESCE(case when ct.environment = 'PRODUCTION' then 'PRODUCTION' else 'NON PRODUCTION' END, 'ALL') AS environment,
    COALESCE(t.name, 'ALL') AS ctType,
    COALESCE(t.type_path, 'ALL') AS ctTypePath,
    COUNT(ct_history.id) as total

    FROM
    public.ct_history
            JOIN
    public.ct ON ct_history.ct_id = ct.id
    join public.ct_type t on t.id = ct.type_id 
    , jsonb_array_elements(ct_history."attributes") as "severity"
    WHERE
    ct_history.event = 'ATTEND'
    and "severity"->>'name' = 'severity'
    and ct_history.creation_date between :from and  :to
    and (ct.id IN (SELECT cts_id FROM tennant))

    GROUP BY GROUPING SETS (
                   ("severity"->>'value'),
            (case when ct.environment = 'PRODUCTION' then 'PRODUCTION' else 'NON PRODUCTION' END, "severity"->>'value' ),
            (t.name,t.type_path, "severity"->>'value'),
            (t.name,t.type_path, case when ct.environment = 'PRODUCTION' then 'PRODUCTION' else 'NON PRODUCTION' END, "severity"->>'value')

            )

    order by severity, environment, cttypepath
     """, nativeQuery = true)
    List<DashboardEntriesAttendedProjection> findDashboardEntriesAttended(@Param("userId") Long userId, @Param("from") LocalDateTime from,
                                                                  @Param("to") LocalDateTime to);


}
