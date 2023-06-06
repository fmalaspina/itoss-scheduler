package com.frsi.itoss.mgr.controllers;

import com.frsi.itoss.mgr.security.AuthenticationFacade;
import com.frsi.itoss.mgr.services.CtEventService;
import com.frsi.itoss.model.commonservices.ResultSetJSONConvertorService;
import com.frsi.itoss.model.dashboard.DashboardEntryCount;
import com.frsi.itoss.model.projections.DashboardEntriesAttendedProjection;
import com.frsi.itoss.model.projections.DashboardEntriesProjection;
import com.frsi.itoss.model.repository.DashboardEntriesRepository;
import com.frsi.itoss.model.repository.UserAccountRepo;
import lombok.extern.java.Log;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.sql.SQLException;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.util.*;

@Log
@RestController
@RequestMapping("/dashboardEntries")
public class DashboardEntriesController {
    @Autowired
    private UserAccountRepo userAccountRepo;
    @Autowired
    DashboardEntriesRepository dashboardEntriesRepository;

    @Autowired
    CtEventService ctService;

    @Autowired
    AuthenticationFacade auth;
    @Autowired
    ResultSetJSONConvertorService convertor;
    @PersistenceContext
    private EntityManager em;


    @RequestMapping(value = "/search/counterWithTotals", method = RequestMethod.GET, produces = "application/json")
    public List<?> findDashboardEntryCountWithTotals(@RequestParam("userId") Long userId,
                                                     @RequestParam("containerId") Long containerId, @RequestParam("environment") String environment,
                                                     @RequestParam("attended") String attended, @RequestParam("supportUserId") Long supportUserId,
                                                     @RequestParam("workgroupIds") String workgroupIds, @RequestParam("locationIds") String locationIds,
                                                     @RequestParam("companyIds") String companyIds, @RequestParam("ctIds") String ctIds) {
//        Query query = em.createNativeQuery(
//                """
//                        WITH tennant AS (SELECT cts_id FROM tennant_cts tc INNER JOIN tennant_users tu ON tu.tennant_id = tc.tennant_id WHERE tu.users_id = ?1) SELECT de.severity as severity, ct.environment as environment, count(ct.*) as count FROM dashboard_entry de inner join ct ct on ct.id = de.ct_id where de.container_id = ?2 and de.ct_id in (select id from ct where id in (select cts_id from tennant)) AND (?3 = '' OR ct.environment = ANY (select unnest(string_to_array(?3, ',')))) AND (?4 = '' OR coalesce(de.attended, false) = cast(?4 as boolean)) AND (?5 = 0 OR ct.support_user_id = ?5) AND ct.state = 'OPERATIONS' group by de.severity, ct.environment  union all   select mcs.status as severity, 'TOTALS' as environment , count(ct.*) as count  from monitor_ct_status mcs inner join ct ct on ct.id = mcs.ct_id  inner join monitor m on m.id = mcs.monitor_id  inner join container c on c.id = m.container_id   where c.id = ?2  and mcs.ct_id in (select id from ct where id in (select cts_id from tennant))     AND (?3 = '' OR ct.environment = ANY (select unnest(string_to_array(?3, ',')))) AND (?5 = 0 OR ct.support_user_id = ?5)  AND ct.state = 'OPERATIONS'  group by mcs.status union all   SELECT 'CT_WITH_DASHBOARDENTRIES', 'TOTALS' as environment, count(distinct ct.id) as count  FROM dashboard_entry de  inner join ct ct on ct.id = de.ct_id  where de.container_id = ?2  and de.ct_id in (select id from ct where id in (select cts_id from tennant)) AND (?3 = '' OR ct.environment = ANY (select unnest(string_to_array(?3, ','))))  AND (?4 = '' OR coalesce(de.attended, false) = cast(?4 as boolean))  AND (?5 = 0 OR ct.support_user_id = ?5)  AND ct.state = 'OPERATIONS'
//                        """);
        Query query = em.createNativeQuery(
                """
                        WITH tennant AS (SELECT cts_id FROM tennant_cts tc INNER JOIN tennant_users tu ON tu.tennant_id = tc.tennant_id WHERE tu.users_id = ?1) 
                                                
                        SELECT de.severity AS severity, ct.environment AS environment, count(ct.*) AS count 
                        FROM dashboard_entry de 
                        INNER JOIN ct ct on ct.id = de.ct_id 
                        WHERE de.container_id = ?2 
                           AND de.ct_id IN (SELECT id FROM ct WHERE id IN (SELECT cts_id FROM tennant)) 
                           AND (?3 = '' OR ct.environment = ANY (SELECT unnest(string_to_array(?3, ',')))) 
                           AND (?4 = '' OR coalesce(de.attended, false) = cast(?4 AS boolean)) 
                           AND (?5 = 0 OR ct.support_user_id = ?5) 
                           AND (?6 = '' OR ct.workgroup_id = ANY (select id from public.workgroups(string_to_array(?6, ',')\\:\\:int[])))
                           AND (?7 = '' OR ct.location_id = ANY (select id from locations(string_to_array(?7, ',')\\:\\:int[]))) 
                           AND (?8 = '' OR ct.company_id = ANY (SELECT unnest(string_to_array(?8, ','))\\:\\:bigint)) 
                           AND (?9 = '' OR ct.id = ANY (SELECT unnest(string_to_array(?9, ','))\\:\\:bigint)) 
                           AND ct.state = 'OPERATIONS' 
                        GROUP BY de.severity, ct.environment  
                           UNION ALL  
                                                
                        SELECT mcs.status AS severity, 'TOTALS' AS environment , count(ct.*) AS count  
                        FROM monitor_ct_status mcs 
                        INNER JOIN ct ct on ct.id = mcs.ct_id  
                        INNER JOIN monitor m on m.id = mcs.monitor_id 
                        INNER JOIN container c on c.id = m.container_id  
                        WHERE c.id = ?2 
                             AND mcs.ct_id IN (SELECT id FROM ct where id IN (SELECT cts_id FROM tennant))     
                             AND (?3 = '' OR ct.environment = ANY (SELECT unnest(string_to_array(?3, ',')))) 
                             AND (?5 = 0 OR ct.support_user_id = ?5)  
                             AND (?6 = '' OR ct.workgroup_id = ANY (select id from public.workgroups(string_to_array(?6, ',')\\:\\:int[]))) 
                             AND (?7 = '' OR ct.location_id = ANY (select id from locations(string_to_array(?7, ',')\\:\\:int[]))) 
                             AND (?8 = '' OR ct.company_id = ANY (SELECT unnest(string_to_array(?8, ','))\\:\\:bigint)) 
                             AND (?9 = '' OR ct.id = ANY (SELECT unnest(string_to_array(?9, ','))\\:\\:bigint)) 
                             AND ct.state = 'OPERATIONS' 
                        GROUP BY mcs.status 
                                                
                        UNION ALL  
                                                
                        SELECT 'CT_WITH_DASHBOARDENTRIES', 'TOTALS' AS environment, count(distinct ct.id) AS count  
                        FROM dashboard_entry de  
                        INNER JOIN ct ct on ct.id = de.ct_id  
                        WHERE de.container_id = ?2 
                        AND de.ct_id IN (SELECT id FROM ct where id IN (SELECT cts_id FROM tennant)) 
                        AND (?3 = '' OR ct.environment = ANY (SELECT unnest(string_to_array(?3, ',')))) 
                        AND (?4 = '' OR coalesce(de.attended, false) = cast(?4 AS boolean)) 
                        AND (?5 = 0 OR ct.support_user_id = ?5) 
                        AND (?6 = '' OR ct.workgroup_id = ANY (select id from public.workgroups(string_to_array(?6, ',')\\:\\:int[])) )  
                        AND (?7 = '' OR ct.location_id = ANY (select id from locations(string_to_array(?7, ',')\\:\\:int[]))) 
                        AND (?8 = '' OR ct.company_id = ANY (SELECT unnest(string_to_array(?8, ','))\\:\\:bigint)) 
                        AND (?9 = '' OR ct.id = ANY (SELECT unnest(string_to_array(?9, ','))\\:\\:bigint))
                        AND ct.state = 'OPERATIONS'
                        """);
        query.setParameter(1, userId);
        query.setParameter(2, containerId);
        query.setParameter(3, environment);
        query.setParameter(4, attended);
        query.setParameter(5, supportUserId);
        query.setParameter(6, workgroupIds);
        query.setParameter(7, locationIds);
        query.setParameter(8, companyIds);
        query.setParameter(9, ctIds);
        try {
            List<Object[]> result = (List<Object[]>) query.getResultList();
            List<DashboardEntryCount> counterResult = new ArrayList<>();
            for (Object[] o : result) {
                DashboardEntryCount counter = new DashboardEntryCount(o[0].toString(), o[1].toString(),
                        Long.valueOf(o[2].toString()));
                counterResult.add(counter);
            }
            return counterResult;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    @RequestMapping(value = "/search/counter", method = RequestMethod.GET, produces = "application/json")
    public List<?> findDashboardEntryCount(@RequestParam("userId") Long userId,
                                           @RequestParam("containerId") Long containerId, @RequestParam("environment") String environment,
                                           @RequestParam("attended") String attended, @RequestParam("supportUserId") Long supportUserId,
                                           @RequestParam("workgroupIds") String workgroupIds, @RequestParam("locationIds") String locationIds,
                                           @RequestParam("companyIds") String companyIds, @RequestParam("ctIds") String ctIds) {
//        Query query = em.createNativeQuery(
//                "WITH tennant AS (SELECT cts_id FROM tennant_cts tc INNER JOIN tennant_users tu ON tu.tennant_id = tc.tennant_id WHERE tu.users_id = ?1) SELECT de.severity as severity, ct.environment as environment, count(ct.*) as count FROM dashboard_entry de inner join ct ct on ct.id = de.ct_id where de.container_id = ?2 and de.ct_id in (select id from ct where id in (select cts_id from tennant)) AND (?3 = '' OR ct.environment = ANY (select unnest(string_to_array(?3, ',')))) AND (?4 = '' OR coalesce(de.attended, false) = cast(?4 as boolean)) AND (?5 = 0 OR ct.support_user_id = ?5) AND ct.state = 'OPERATIONS' group by de.severity, ct.environment");
//
        Query query = em.createNativeQuery(
                """
                                        WITH tennant AS (SELECT cts_id FROM tennant_cts tc INNER JOIN tennant_users tu ON tu.tennant_id = tc.tennant_id WHERE tu.users_id = ?1) SELECT de.severity as severity, ct.environment as environment, count(ct.*) as count FROM dashboard_entry de inner join ct ct on ct.id = de.ct_id where de.container_id = ?2 and de.ct_id in (select id from ct where id in (select cts_id from tennant)) AND (?3 = '' OR ct.environment = ANY (select unnest(string_to_array(?3, ',')))) AND (?4 = '' OR coalesce(de.attended, false) = cast(?4 as boolean)) AND (?5 = 0 OR ct.support_user_id = ?5)
                                        AND (?6 = '' OR ct.workgroup_id = ANY (select id from workgroups(ARRAY[?6]))) AND (?7 = '' OR ct.location_id = ANY (select id from locations(ARRAY[?7]))) AND (?8 = '' OR ct.company_id = ANY (SELECT unnest(string_to_array(?8, ',')))) AND (?9 = '' OR ct.id = ANY (SELECT unnest(string_to_array(?9, ',')))) AND ct.state = 'OPERATIONS' group by de.severity, ct.environment
                                
                        """);
        query.setParameter(1, userId);
        query.setParameter(2, containerId);
        query.setParameter(3, environment);
        query.setParameter(4, attended);
        query.setParameter(5, supportUserId);
        query.setParameter(6, workgroupIds);
        query.setParameter(7, locationIds);
        query.setParameter(8, companyIds);
        query.setParameter(9, ctIds);
        List<Object[]> result = (List<Object[]>) query.getResultList();
        List<DashboardEntryCount> counterResult = new ArrayList<>();
        for (Object[] o : result) {
            DashboardEntryCount counter = new DashboardEntryCount(o[0].toString(), o[1].toString(),
                    Long.valueOf(o[2].toString()));
            counterResult.add(counter);
        }
        return counterResult;
    }


    @RequestMapping(value = "/search/findByContainerAndSeverityPageable", method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity<?> findByContainerAndSeverityPageable(@RequestParam("userId") Long userId,
                                                                @RequestParam("containerId") Long containerId, @RequestParam("severity") String severity,
                                                                @RequestParam("environment") String environment, @RequestParam("attended") String attended,
                                                                @RequestParam("supportUserId") Long supportUserId,
                                                                @RequestParam("workgroupIds") String workgroupIds, @RequestParam("locationIds") String locationIds,
                                                                @RequestParam("companyIds") String companyIds,
                                                                @RequestParam("ctIds") String ctIds,
                                                                @RequestParam("page") Long page, @RequestParam("size") Long size, @RequestParam("sort") String sort) throws SQLException, Exception {
        return findBySpecificationPageable(userId, 0L, ctIds, "", 0L, environment, "", 0L, severity, 0L, attended, supportUserId, "", "", containerId.toString(), workgroupIds, locationIds, companyIds, page, size, sort);
    }


    @RequestMapping(value = "/search/findByContainerAndSeverity", method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity<?> findByContainerAndSeverity(@RequestParam("userId") Long userId,
                                                        @RequestParam("containerId") Long containerId, @RequestParam("severity") String severity,
                                                        @RequestParam("environment") String environment, @RequestParam("attended") String attended,
                                                        @RequestParam("supportUserId") Long supportUserId) throws SQLException, Exception {
        String query = "WITH tennant AS (SELECT cts_id FROM tennant_cts tc INNER JOIN tennant_users tu ON tu.tennant_id = tc.tennant_id WHERE tu.users_id = "
                       + userId
                       + ") SELECT COALESCE(de.modified_at, de.created_at) AS date, de.severity,  de.object, de.attended, de.metric_payload_data, de.rule_description AS event_rule, ct.name, ct.attributes, ct.environment, co.name company_name, ctt.name AS type_name, cts.down, cts.last_status_change AS date_status FROM dashboard_entry de INNER JOIN ct ct on ct.id = de.ct_id INNER JOIN ct_type ctt on ctt.id = ct.type_id INNER JOIN company co on co.id = ct.company_id INNER JOIN ct_status cts on cts.id = ct.id WHERE ct.id IN (SELECT cts_id FROM tennant) AND de.container_id = "
                       + containerId + " AND de.severity = '" + severity + "' " + " AND ('" + environment
                       + "' = '' OR ct.environment = ANY (select unnest(string_to_array('" + environment + "', ',')))) AND ('"
                       + attended + "' = '' " + " OR coalesce(de.attended, false) = cast('"
                       + (attended.equals("") ? "false" : attended) + "' as boolean))" + " AND (" + supportUserId
                       + "=0 OR ct.support_user_id=" + supportUserId
                       + ") AND ct.state = 'OPERATIONS' ORDER BY de.score DESC, de.created_at ASC";
        return convertor.execute(query);
    }

    @RequestMapping(value = "/search/findByContainer", method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity<?> findByContainer(@RequestParam("userId") Long userId,
                                             @RequestParam("containerId") Long containerId, @RequestParam("environment") String environment,
                                             @RequestParam("attended") String attended, @RequestParam("supportUserId") Long supportUserId)
            throws SQLException, Exception {
        String query = "WITH tennant AS (SELECT cts_id FROM tennant_cts tc INNER JOIN tennant_users tu ON tu.tennant_id = tc.tennant_id WHERE tu.users_id = "
                       + userId
                       + ") SELECT COALESCE(de.modified_at, de.created_at) AS date, last_change - created_at as diff_date, de.severity, de.object, de.attended, de.metric_payload_data, de.rule_description AS event_rule, ct.name, ct.attributes, ct.environment, co.name company_name, ctt.name AS type_name, cts.down, cts.last_status_change AS date_status FROM dashboard_entry de INNER JOIN ct ct on ct.id = de.ct_id INNER JOIN ct_type ctt on ctt.id = ct.type_id INNER JOIN company co on co.id = ct.company_id INNER JOIN ct_status cts on cts.id = ct.id WHERE ct.id IN (SELECT cts_id FROM tennant) AND de.container_id = "
                       + containerId + " AND ('" + environment
                       + "' = '' OR ct.environment = ANY (select unnest(string_to_array('" + environment + "', ',')))) AND ('"
                       + attended + "' = '' " + " OR coalesce(de.attended, false) = cast('"
                       + (attended.equals("") ? "false" : attended) + "' as boolean))" + " AND (" + supportUserId
                       + "=0 OR ct.support_user_id=" + supportUserId
                       + ") AND ct.state = 'OPERATIONS' ORDER BY de.score DESC, de.created_at ASC";
        // return ResponseEntity.ok(jdbcTemplate.queryForList(query));
        return convertor.execute(query);
    }

    @RequestMapping(value = "/search/findBySpecification", method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity<?> findBySpecification(@RequestParam("userId") Long userId, @RequestParam("ctId") Long ctId,
                                                 @RequestParam("ctIds") String ctIds, @RequestParam("ctName") String ctName, @RequestParam("ctType") Long ctType,
                                                 @RequestParam("ctEnvironment") String ctEnvironment, @RequestParam("metricCategory") String metricCategory,
                                                 @RequestParam("monitorId") Long monitorId, @RequestParam("severities") String severities,
                                                 @RequestParam("companyId") Long companyId, @RequestParam("attended") String attended,
                                                 @RequestParam("supportUserId") Long supportUserId, @RequestParam("excludeSeverities") String excludeSeverities,
                                                 @RequestParam("monitorIds") String monitorIds, @RequestParam("containerIds") String containerIds,
                                                 @RequestParam("scoreFrom") int scoreFrom, @RequestParam("scoreTo") String scoreTo)


            throws SQLException, Exception {
        StringBuilder sb = new StringBuilder();
        sb.append(
                "WITH tennant AS (SELECT cts_id FROM tennant_cts tc INNER JOIN tennant_users tu ON tu.tennant_id = tc.tennant_id WHERE tu.users_id = "
                + userId
                + ") SELECT coalesce(de.modified_at, de.created_at) AS date, de.created_at, de.severity, de.rule_description event_rule, de.attended, de.object, de.score, metric_payload_data, mo.name monitor, me.name metric, me.metric_category, c.name container, c.attributes container_attributes,ct.id ct_id, ct.name ct_name, ct.attributes ct_attributes, ctt.name ct_type, ct.environment ct_environment,  co.name company, l.name AS location FROM dashboard_entry de INNER JOIN container c ON c.id = de.container_id INNER JOIN monitor mo ON mo.id = de.monitor_id INNER JOIN metric me ON me.id = mo.metric_id INNER JOIN ct ON ct.id = de.ct_id INNER JOIN ct_type ctt ON ctt.id = ct.type_id INNER JOIN company co ON co.id = ct.company_id INNER JOIN location l ON l.id = ct.location_id WHERE ct.id IN (SELECT cts_id FROM tennant) AND (");
        sb.append(ctId);
        sb.append(" = 0 OR ct.id = ");
        sb.append(ctId);
        sb.append(") AND (");
        sb.append("'");
        sb.append(ctName);
        sb.append("'");
        sb.append(" = '' OR ct.name LIKE Concat('%',");
        sb.append("'");
        sb.append(ctName);
        sb.append("'");
        sb.append(",'%')) AND (");
        sb.append(ctType);
        sb.append(" = 0 OR ct.type_id = ");
        sb.append(ctType);
        sb.append(") AND (");
        sb.append("'");
        sb.append(ctEnvironment);
        sb.append("'");
        sb.append(" = '' OR ct.environment = ");
        sb.append("'");
        sb.append(ctEnvironment);
        sb.append("'");
        sb.append(") AND (");
        sb.append("'");
        sb.append(metricCategory);
        sb.append("'");
        sb.append(" = '' OR me.metric_category = ");
        sb.append("'");
        sb.append(metricCategory);
        sb.append("'");
        sb.append(") AND (");
        sb.append(monitorId);
        sb.append(" = 0 OR mo.id = ");
        sb.append(monitorId);
        sb.append(") AND (");
        sb.append(supportUserId);
        sb.append(" = 0 OR ct.support_user_id = ");
        sb.append(supportUserId);
        // scoreFrom
        sb.append(") AND (");
        sb.append(scoreFrom);
        sb.append(" = 0 OR de.score >= ");
        sb.append(scoreFrom);
        // scoreTo
        sb.append(") AND (");
        sb.append(scoreTo);
        sb.append(" = 0 OR de.score <= ");
        sb.append(scoreTo);
        sb.append(") AND (");
        sb.append("'");
        sb.append(severities);
        sb.append("'");
        sb.append(" = '' OR de.severity = ANY (select unnest(string_to_array(");
        sb.append("'");
        sb.append(severities);
        sb.append("'");
        sb.append(", ',')))) AND  (");
        sb.append("'");
        sb.append(excludeSeverities);
        sb.append("'");
        sb.append(" = '' OR NOT (de.severity = ANY (select unnest(string_to_array(");
        sb.append("'");
        sb.append(excludeSeverities);
        sb.append("'");
        sb.append(", ','))))) AND  (");
        sb.append("'");
        sb.append(ctIds);
        sb.append("'");
        sb.append(" = '' OR (ct.id = ANY (select unnest(string_to_array(");
        sb.append("'");
        sb.append(ctIds);
        sb.append("'");
        sb.append(", ','))::bigint))) AND  (");
        sb.append("'");
        sb.append(containerIds);
        sb.append("'");
        sb.append(" = '' OR de.container_id = ANY (select unnest(string_to_array(");
        sb.append("'");
        sb.append(containerIds);
        sb.append("'");
        sb.append(", ','))::bigint)) AND  (");
        sb.append("'");
        sb.append(monitorIds);
        sb.append("'");
        sb.append(" = '' OR mo.id = ANY (select unnest(string_to_array(");
        sb.append("'");
        sb.append(monitorIds);
        sb.append("'");
        sb.append(", ','))::bigint)) AND  (");
        sb.append(companyId);
        sb.append(" = 0 OR co.id = ");
        sb.append(companyId);
        sb.append(") AND ('" + attended + "' = '' OR coalesce(de.attended, false) = cast('"
                  + (attended.equals("") ? "false" : attended)
                  + "' as boolean)) AND ct.state = 'OPERATIONS' ORDER BY de.score DESC, 1 ASC");
        // return ResponseEntity.ok(jdbcTemplate.queryForList(sb.toString()));
        return convertor.execute(sb.toString());

    }

    @RequestMapping(value = "/search/findBySpecificationPageable", method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity<?> findBySpecificationPageable(@RequestParam("userId") Long userId,
                                                         @RequestParam("ctId") Long ctId, @RequestParam("ctIds") String ctIds, @RequestParam("ctName") String ctName,
                                                         @RequestParam("ctType") Long ctType, @RequestParam("ctEnvironment") String ctEnvironment,
                                                         @RequestParam("metricCategory") String metricCategory, @RequestParam("monitorId") Long monitorId,
                                                         @RequestParam("severities") String severities, @RequestParam("companyId") Long companyId,
                                                         @RequestParam("attended") String attended, @RequestParam("supportUserId") Long supportUserId,
                                                         @RequestParam("excludeSeverities") String excludeSeverities, @RequestParam("monitorIds") String monitorIds,
                                                         @RequestParam("containerIds") String containerIds,
                                                         @RequestParam("workgroupIds") String workgroupIds, @RequestParam("locationIds") String locationIds,
                                                         @RequestParam("companyIds") String companyIds,
                                                         @RequestParam("page") Long page,
                                                         @RequestParam("size") Long size, @RequestParam("sort") String sort)
            throws SQLException, Exception {
        String select =
                "WITH tennant AS (SELECT cts_id FROM tennant_cts tc INNER JOIN tennant_users tu ON tu.tennant_id = tc.tennant_id WHERE tu.users_id = "
                + userId
                + ") SELECT coalesce(de.modified_at, de.created_at) as date, de.created_at, de.severity, de.rule_description event_rule, de.attended, de.object, de.score, metric_payload_data, mo.name monitor, me.name metric, me.metric_category, c.name container, c.attributes container_attributes,ct.id ct_id, ct.name ct_name, ct.attributes ct_attributes, ctt.name ct_type, ct.environment ct_environment,  co.name company, l.name AS location ";
        String selectCount = "WITH tennant AS (SELECT cts_id FROM tennant_cts tc INNER JOIN tennant_users tu ON tu.tennant_id = tc.tennant_id WHERE tu.users_id = " + userId + ") SELECT count(*) ";
        String selectSeverityCount = "WITH tennant AS (SELECT cts_id FROM tennant_cts tc INNER JOIN tennant_users tu ON tu.tennant_id = tc.tennant_id WHERE tu.users_id = " + userId + ") SELECT count(*), de.severity ";
        String groupBySeverityCount = " GROUP BY de.severity ";
        String from = "FROM dashboard_entry de INNER JOIN container c ON c.id = de.container_id INNER JOIN monitor mo ON mo.id = de.monitor_id INNER JOIN metric me ON me.id = mo.metric_id INNER JOIN ct ON ct.id = de.ct_id INNER JOIN ct_type ctt ON ctt.id = ct.type_id INNER JOIN company co ON co.id = ct.company_id INNER JOIN location l ON l.id = ct.location_id ";
//        String where = " WHERE ct.id IN (SELECT cts_id FROM tennant) AND (" + ctId + " = 0 OR ct.id = " + ctId + ") AND (" + ctType + " = 0 OR ct.type_id = " + ctType + ") AND ('" + ctEnvironment + "' = '' OR ct.environment = '" + ctEnvironment + "') AND ('" + metricCategory + "' = '' OR me.metric_category = '" + metricCategory + "') AND (" + monitorId + " = 0 OR mo.id = " + monitorId + ") AND (" + supportUserId + " = 0 OR ct.support_user_id = " + supportUserId + ") AND ('" + severities + "' = '' OR de.severity = ANY (select unnest(string_to_array('" + severities + "', ',')))) AND  ('" + excludeSeverities + "' = '' OR NOT (de.severity = ANY (select unnest(string_to_array('" + excludeSeverities + "', ','))))) AND  ('" + ctIds + "'" + " = '' OR (ct.id = ANY (select unnest(string_to_array('" + ctIds + "', ','))\\:\\:bigint))) AND  ('" + containerIds + "' = '' OR de.container_id = ANY (select unnest(string_to_array('" + containerIds + "', ','))\\:\\:bigint)) AND  ('" + monitorIds + "'" + " = '' OR mo.id = ANY (select unnest(string_to_array('" + monitorIds + "', ','))\\:\\:bigint)) AND  (" + companyId + " = 0 OR co.id = " + companyId + ") AND ('" + attended + "' = '' OR coalesce(de.attended, false) = cast('" + (attended.equals("") ? "false" : attended) + "' as boolean)) AND ct.state = 'OPERATIONS' ";
        String where = " WHERE ct.id IN (SELECT cts_id FROM tennant) AND (" + ctId + " = 0 OR ct.id = " + ctId + ") AND (" + ctType + " = 0 OR ct.type_id = " + ctType +
                       ") AND ('" + ctEnvironment + "' = '' OR ct.environment = '" + ctEnvironment + "') AND ('" + metricCategory + "' = '' OR me.metric_category = '" +
                       metricCategory +
                       "') AND (" + monitorId + " = 0 OR mo.id = " + monitorId + ") AND (" + supportUserId + " = 0 OR ct.support_user_id = " + supportUserId +
                       ") AND ('" + severities + "' = '' OR de.severity = ANY (select unnest(string_to_array('" + severities + "', ',')))) AND  ('" + excludeSeverities + "' = '' OR NOT (de.severity = ANY (select unnest(string_to_array('" + excludeSeverities + "', ','))))) AND  ('" + ctIds + "'" + " = '' OR (ct.id = ANY (select unnest(string_to_array('" + ctIds + "', ','))::bigint))) AND  ('" + containerIds + "' = '' OR de.container_id = ANY (select unnest(string_to_array('" + containerIds + "', ','))::bigint)) AND  ('" + monitorIds + "'" + " = '' OR mo.id = ANY (select unnest(string_to_array('" + monitorIds + "', ','))::bigint)) AND  (" + companyId + " = 0 OR co.id = " + companyId + ") AND ('" + attended + "' = '' OR coalesce(de.attended, false) = cast('" + (attended.equals("") ? "false" : attended) + "' as boolean)) AND ct.state = 'OPERATIONS' " +
                       " AND ('" + workgroupIds + "'= '' OR ct.workgroup_id = ANY (select id from workgroups(string_to_array('" + workgroupIds + "', ',')::int[])))" +
                       " AND ('" + locationIds + "'= '' OR ct.location_id = ANY (select id from locations(string_to_array('" + locationIds + "', ',')::int[])))" +

                       " AND ('" + companyIds + "'= '' OR ct.company_id = ANY (SELECT unnest(string_to_array('" + companyIds + "', ','))::bigint))";
        String orderBy;
        if (sort != null && !sort.isBlank()) {
            String[] sortParts = sort.split(",");
            orderBy = " ORDER BY " + sortParts[0] + " " + (sortParts.length > 1 ? sortParts[1] : "") + ", 1 ASC";
        } else {
            orderBy = " ORDER BY de.score DESC, 1 ASC";

        }
        String limit = " LIMIT " + size + " OFFSET " + page * size;
        String queryData = select + from + where + orderBy + limit;
        String queryCount = selectCount + from + where;
        String querySeverityCount = selectSeverityCount + from + where + groupBySeverityCount;
        JSONArray jaData = convertor.executeQuery(queryData);
        JSONArray jaCount = convertor.executeQuery(queryCount);
        JSONArray jaSeverity = convertor.executeQuery(querySeverityCount);
        JSONObject jaAll = new JSONObject();
        jaAll.put("page", jaCount.get(0));
        jaAll.put("data", jaData);
        jaAll.put("summary", jaSeverity);
        return ResponseEntity.ok(jaAll.toString());


    }

    @RequestMapping(value = "/search/findBySpecificationWithScorePageable", method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity<?> findBySpecificationWithScorePageable(@RequestParam("userId") Long userId,
                                                         @RequestParam("ctId") Long ctId, @RequestParam("ctIds") String ctIds, @RequestParam("ctName") String ctName,
                                                         @RequestParam("ctType") Long ctType, @RequestParam("ctEnvironment") String ctEnvironment,
                                                         @RequestParam("metricCategory") String metricCategory, @RequestParam("monitorId") Long monitorId,
                                                         @RequestParam("severities") String severities, @RequestParam("companyId") Long companyId,
                                                         @RequestParam("attended") String attended, @RequestParam("supportUserId") Long supportUserId,
                                                         @RequestParam("excludeSeverities") String excludeSeverities, @RequestParam("monitorIds") String monitorIds,
                                                         @RequestParam("containerIds") String containerIds,
                                                         @RequestParam("workgroupIds") String workgroupIds, @RequestParam("locationIds") String locationIds,
                                                         @RequestParam("companyIds") String companyIds,
                                                         @RequestParam("scoreFrom") int scoreFrom, @RequestParam("scoreTo") String scoreTo,
                                                         @RequestParam("page") Long page,
                                                         @RequestParam("size") Long size, @RequestParam("sort") String sort)
            throws SQLException, Exception {
        String select =
                "WITH tennant AS (SELECT cts_id FROM tennant_cts tc INNER JOIN tennant_users tu ON tu.tennant_id = tc.tennant_id WHERE tu.users_id = "
                + userId
                + ") SELECT coalesce(de.modified_at, de.created_at) as date, de.created_at, de.severity, de.rule_description event_rule, de.attended, de.object, de.score, metric_payload_data, mo.name monitor, me.name metric, me.metric_category, c.name container, c.attributes container_attributes,ct.id ct_id, ct.name ct_name, ct.attributes ct_attributes, ctt.name ct_type, ct.environment ct_environment,  co.name company, l.name AS location ";
        String selectCount = "WITH tennant AS (SELECT cts_id FROM tennant_cts tc INNER JOIN tennant_users tu ON tu.tennant_id = tc.tennant_id WHERE tu.users_id = " + userId + ") SELECT count(*) ";
        String selectSeverityCount = "WITH tennant AS (SELECT cts_id FROM tennant_cts tc INNER JOIN tennant_users tu ON tu.tennant_id = tc.tennant_id WHERE tu.users_id = " + userId + ") SELECT count(*), de.severity ";
        String groupBySeverityCount = " GROUP BY de.severity ";
        String from = "FROM dashboard_entry de INNER JOIN container c ON c.id = de.container_id INNER JOIN monitor mo ON mo.id = de.monitor_id INNER JOIN metric me ON me.id = mo.metric_id INNER JOIN ct ON ct.id = de.ct_id INNER JOIN ct_type ctt ON ctt.id = ct.type_id INNER JOIN company co ON co.id = ct.company_id INNER JOIN location l ON l.id = ct.location_id ";
//        String where = " WHERE ct.id IN (SELECT cts_id FROM tennant) AND (" + ctId + " = 0 OR ct.id = " + ctId + ") AND (" + ctType + " = 0 OR ct.type_id = " + ctType + ") AND ('" + ctEnvironment + "' = '' OR ct.environment = '" + ctEnvironment + "') AND ('" + metricCategory + "' = '' OR me.metric_category = '" + metricCategory + "') AND (" + monitorId + " = 0 OR mo.id = " + monitorId + ") AND (" + supportUserId + " = 0 OR ct.support_user_id = " + supportUserId + ") AND ('" + severities + "' = '' OR de.severity = ANY (select unnest(string_to_array('" + severities + "', ',')))) AND  ('" + excludeSeverities + "' = '' OR NOT (de.severity = ANY (select unnest(string_to_array('" + excludeSeverities + "', ','))))) AND  ('" + ctIds + "'" + " = '' OR (ct.id = ANY (select unnest(string_to_array('" + ctIds + "', ','))\\:\\:bigint))) AND  ('" + containerIds + "' = '' OR de.container_id = ANY (select unnest(string_to_array('" + containerIds + "', ','))\\:\\:bigint)) AND  ('" + monitorIds + "'" + " = '' OR mo.id = ANY (select unnest(string_to_array('" + monitorIds + "', ','))\\:\\:bigint)) AND  (" + companyId + " = 0 OR co.id = " + companyId + ") AND ('" + attended + "' = '' OR coalesce(de.attended, false) = cast('" + (attended.equals("") ? "false" : attended) + "' as boolean)) AND ct.state = 'OPERATIONS' ";
        String where = " WHERE ct.id IN (SELECT cts_id FROM tennant) AND (" + ctId + " = 0 OR ct.id = " + ctId + ") AND (" + ctType + " = 0 OR ct.type_id = " + ctType +
                       ") AND ('" + ctEnvironment + "' = '' OR ct.environment = '" + ctEnvironment + "') AND ('" + metricCategory + "' = '' OR me.metric_category = '" +
                       metricCategory +
                       "') AND (" + monitorId + " = 0 OR mo.id = " + monitorId + ") AND (" + supportUserId + " = 0 OR ct.support_user_id = " + supportUserId +
                       "') AND (" + scoreFrom + " = 0 OR de.score >= " + scoreFrom + ") AND (" + scoreTo + " = 0 OR de.score <= " + scoreTo +
                       ") AND ('" + severities + "' = '' OR de.severity = ANY (select unnest(string_to_array('" + severities + "', ',')))) AND  ('" + excludeSeverities + "' = '' OR NOT (de.severity = ANY (select unnest(string_to_array('" + excludeSeverities + "', ','))))) AND  ('" + ctIds + "'" + " = '' OR (ct.id = ANY (select unnest(string_to_array('" + ctIds + "', ','))::bigint))) AND  ('" + containerIds + "' = '' OR de.container_id = ANY (select unnest(string_to_array('" + containerIds + "', ','))::bigint)) AND  ('" + monitorIds + "'" + " = '' OR mo.id = ANY (select unnest(string_to_array('" + monitorIds + "', ','))::bigint)) AND  (" + companyId + " = 0 OR co.id = " + companyId + ") AND ('" + attended + "' = '' OR coalesce(de.attended, false) = cast('" + (attended.equals("") ? "false" : attended) + "' as boolean)) AND ct.state = 'OPERATIONS' " +
                       " AND ('" + workgroupIds + "'= '' OR ct.workgroup_id = ANY (select id from workgroups(string_to_array('" + workgroupIds + "', ',')::int[])))" +
                       " AND ('" + locationIds + "'= '' OR ct.location_id = ANY (select id from locations(string_to_array('" + locationIds + "', ',')::int[])))" +

                       " AND ('" + companyIds + "'= '' OR ct.company_id = ANY (SELECT unnest(string_to_array('" + companyIds + "', ','))::bigint))";
        String orderBy;
        if (sort != null && !sort.isBlank()) {
            String[] sortParts = sort.split(",");
            orderBy = " ORDER BY " + sortParts[0] + " " + (sortParts.length > 1 ? sortParts[1] : "") + ", 1 ASC";
        } else {
            orderBy = " ORDER BY de.score DESC, 1 ASC";

        }
        String limit = " LIMIT " + size + " OFFSET " + page * size;
        String queryData = select + from + where + orderBy + limit;
        String queryCount = selectCount + from + where;
        String querySeverityCount = selectSeverityCount + from + where + groupBySeverityCount;
        JSONArray jaData = convertor.executeQuery(queryData);
        JSONArray jaCount = convertor.executeQuery(queryCount);
        JSONArray jaSeverity = convertor.executeQuery(querySeverityCount);
        JSONObject jaAll = new JSONObject();
        jaAll.put("page", jaCount.get(0));
        jaAll.put("data", jaData);
        jaAll.put("summary", jaSeverity);
        return ResponseEntity.ok(jaAll.toString());


    }

    @NotNull
    private Map<String, Integer> calculateScores(List<DashboardEntriesProjection> results) {
        // Map to store severity counts
        Map<String, Integer> scores = new HashMap<>();
        // Populate severities map with query results
        for (var result : results.stream().filter(Objects::nonNull)
                .filter(de -> de.getCtType().equals("ALL") && de.getEnvironment().equals("ALL") && de.getMetricCategory().equals("ALL"))
                .toList()) {
            String score = result.getScore();
            int count = result.getTotal();
            scores.put(score, count);
        }
        return scores;
    }

    @NotNull
    private List<TypeScore> calculateTypeScores(List<DashboardEntriesProjection> results) {
        // iterate through results and populate TypeScore class
        List<TypeScore> typeScores = new ArrayList<>();
        for (var result : results.stream().filter(Objects::nonNull)
                .filter(de -> !de.getCtType().equals("ALL")
                              && !de.getEnvironment().equals("ALL")
                              && !de.getMetricCategory().equals("ALL"))
                .toList()) {
            String name = result.getCtType();
            Long id = result.getCtTypeId();
            String path = result.getCtTypePath();
            // map to hold the scores for this type
            Map<String, Integer> scores = new HashMap<>();
            // iterate over the same type and summarize scores if they exist or initialize them if not
            for (var result2 : results.stream().filter(Objects::nonNull)
                    .filter(de -> de.getCtType().equals(name) && de.getEnvironment().equals("ALL")
                                  && de.getMetricCategory().equals("ALL"))
                    .toList()) {
                if (scores.containsKey(result2.getScore())) {
                    scores.put(result2.getScore(), scores.get(result2.getScore()) + result2.getTotal());
                } else {
                    scores.put(result2.getScore(), result2.getTotal());
                }
            }
            // initialize list that will be populated with EnvironmentScore objects for this type
            List<EnvironmentScore> environmentScores = new ArrayList<>();
            // populate List<EnvironmentScore> with not null results
            for (var envResult : results.stream().filter(Objects::nonNull)
                    .filter(de -> de.getCtType().equals(name) && !de.getEnvironment().equals("ALL")
                                  && !de.getMetricCategory().equals("ALL"))
                    .toList()) {
                String envName = envResult.getEnvironment();
                Map<String, Integer> envScores = new HashMap<>();
                // iterate over the same environment and summarize scores if they exist or initialize them if not
                for (var envResult2 : results.stream().filter(Objects::nonNull)
                        .filter(de -> de.getCtType().equals(name) && de.getEnvironment().equals(envName)
                                      && !de.getMetricCategory().equals("ALL"))
                        .toList()) {
                    if (envScores.containsKey(envResult2.getScore())) {
                        envScores.put(envResult2.getScore(), envScores.get(envResult2.getScore()) + envResult2.getTotal());
                    } else {
                        envScores.put(envResult2.getScore(), envResult2.getTotal());
                    }
                }
                // check if the environment has already been added to the list
                // if it has, add the count to the existing environment
                // if it hasn't, create a new environment and add it to the list
                if (environmentScores.stream().anyMatch(es -> es.getName().equals(envName))) {
                    environmentScores.stream().filter(es -> es.getName().equals(envName)).forEach(es -> es.getScores().putAll(envScores));
                } else {
                    environmentScores.add(new EnvironmentScore(envName, envScores));
                }
            }
            // initialize list that will be populated with CategoryScore objects for this type
            List<CategoryScore> categoryScores = new ArrayList<>();
            // populate List<CategoryScore> with not null results
            for (var catResult : results.stream().filter(Objects::nonNull)
                    .filter(de -> de.getCtType().equals(name) && !de.getMetricCategory().equals("ALL")
                                  && !de.getEnvironment().equals("ALL"))
                    .toList()) {
                String catName = catResult.getMetricCategory();
                Map<String, Integer> catScores = new HashMap<>();
                // check if the severity has already been added to the map
                // if it has, add the count to the existing severity
                // if it hasn't, create a new severity and add it to the map
                catResult.getScore();
                catScores.put(catResult.getScore(), catResult.getTotal());
                // check if the category has already been added to the list
                // if it has, add the severity to the existing CategoryScore object
                // if it hasn't, create a new CategoryScore object and add it to the list
                if (categoryScores.stream().anyMatch(cs -> cs.getName().equals(catName))) {
                    categoryScores.stream().filter(cs -> cs.getName().equals(catName)).forEach(cs -> cs.getScores().put(catResult.getScore(), catResult.getTotal()));
                } else {
                    CategoryScore categoryScore = new CategoryScore(catName, catScores);
                    categoryScores.add(categoryScore);
                }

            }
            // check if the type has already been added to the list
            // if it has, add the severity map to the existing TypeScore object
            // if it hasn't, create a new TypeScore object and add it to the list
            if (typeScores.stream().anyMatch(ts -> ts.getName().equals(name))) {
                typeScores.stream().filter(ts -> ts.getName().equals(name)).forEach(ts -> ts.getScores().putAll(scores));
            } else {
                TypeScore typeScore = new TypeScore(id, name, path, scores, environmentScores, categoryScores);
                typeScores.add(typeScore);
            }
        }
        return typeScores;

    }

    @NotNull
    private List<EnvironmentScore> calculateEnvironmentScores(List<DashboardEntriesProjection> results) {
        List<EnvironmentScore> environmentScores = new ArrayList<>();
        for (var result : results.stream().filter(Objects::nonNull)
                .filter(de -> de.getCtType().equals("ALL") && !de.getEnvironment().equals("ALL") && de.getMetricCategory().equals("ALL"))
                .toList()) {
            String name = result.getEnvironment();
            Map<String, Integer> scores = new HashMap<>();
            for (var result2 : results.stream().filter(Objects::nonNull)
                    .filter(de -> de.getEnvironment().equals(name) && de.getCtType().equals("ALL") && de.getMetricCategory().equals("ALL"))
                    .toList()) {
                String score = result2.getScore();
                int count = result2.getTotal();
                if (scores.containsKey(score)) {
                    scores.put(score, scores.get(score) + count);
                } else {
                    scores.put(score, count);
                }
            }
            if (environmentScores.stream().anyMatch(es -> es.getName().equals(name))) {
                environmentScores.stream().filter(es -> es.getName().equals(name)).forEach(es -> es.getScores().putAll(scores));
            } else {
                EnvironmentScore environmentScore = new EnvironmentScore(name, scores);
                environmentScores.add(environmentScore);
            }


        }
        return environmentScores;
    }

    @GetMapping("/totalsByEnvironmentAndScore")
    public ResponseEntity<?> getDETotalsByEnvironmentAndScore(@RequestParam(value = "low") int low,
                                                                @RequestParam(value = "medium") int medium, @RequestParam("high") int high,
                                                                @RequestParam("highest") int highest) {
        var user = auth.getUserAccount();

        var results = dashboardEntriesRepository.findDashboardEntriesByScore(user.getId(),low,medium,high,highest);
        Map<String, Object> dashboardData = new HashMap<>();
        Map<String, Integer> scores = calculateScores(results);
        var typeScores = calculateTypeScores(results);
        var environmentScores = calculateEnvironmentScores(results);
        var categoryScores = calculateCategoryScores(results);
        dashboardData.put("scores", scores);
        dashboardData.put("typeScores", typeScores);
        dashboardData.put("environmentScores", environmentScores);
        dashboardData.put("categoryScores", categoryScores);
        return ResponseEntity.ok(dashboardData);
    }

    @GetMapping("/totalsAttendedLast7Days")
    public ResponseEntity<?> getDETotalsAttendedLast7Days() throws Exception {
        LocalDateTime now = LocalDateTime.now();
        final LocalDateTime dateFrom = now.minusDays(7);
        return getDETotalsAttended(dateFrom, now);

    }

    @GetMapping("/totalsAttendedLast30Days")
    public ResponseEntity<?> getDETotalsAttendedLast30Days() throws ParseException {
        LocalDateTime now = LocalDateTime.now();
        final LocalDateTime dateFrom = now.minusDays(30);
        return getDETotalsAttended(dateFrom, now);


    }
    // endpoint that getDRTotalsAttended of last day
    @GetMapping("/totalsAttendedLastDay")
    public ResponseEntity<?> getDETotalsAttendedLastDay() throws ParseException {
        // calculate date from and date to using current date
        LocalDateTime now = LocalDateTime.now();

        final LocalDateTime dateFrom = now.minusDays(1);
        return getDETotalsAttended(dateFrom, now);

    }

    @GetMapping("/totalsAttended")
    public ResponseEntity<?> getDETotalsAttended(@RequestParam("dateFrom") LocalDateTime from, @RequestParam("dateTo") LocalDateTime to) throws ParseException {

        var user = auth.getUserAccount();

        var results = dashboardEntriesRepository.findDashboardEntriesAttended(user.getId(), from, to);
        Map<String, Object> dashboardData = new HashMap<>();
        Map<String, Integer> severities = calculateAttendedSeverities(results);
        var typeSeverities = calculateAttendedTypeSeverities(results);
        var environmenSeverities = calculateAttendedEnvironmentSeverities(results);
//
//
        dashboardData.put("severities", severities);
        dashboardData.put("typeSeverities", typeSeverities);
        dashboardData.put("environmentSeverities", environmenSeverities);

        return ResponseEntity.ok(dashboardData);

    }
    @NotNull
    private Map<String, Integer> calculateAttendedSeverities(List<DashboardEntriesAttendedProjection> results) {

        // Map to store severity counts
        Map<String, Integer> severities = new HashMap<>();

        // Populate severities map with query results
        for (var result : results.stream().filter(Objects::nonNull)
                .filter(de -> de.getCtType().equals("ALL") && de.getEnvironment().equals("ALL"))
                .toList()) {
            String severity = result.getSeverity();
            int count = result.getTotal();
            severities.put(severity, count);
        }
        return severities;
    }

    // method to populate TypeSeverity class
    @NotNull
    private List<TypeAttendedSeverity> calculateAttendedTypeSeverities(List<DashboardEntriesAttendedProjection> results) {
        // iterate through results and populate TypeSeverity class
        List<TypeAttendedSeverity> typeSeverities = new ArrayList<>();
        for (var result : results.stream().filter(Objects::nonNull)
                .filter(de -> !de.getCtType().equals("ALL")
                              && !de.getEnvironment().equals("ALL")
                )
                .toList()) {
            String name = result.getCtType();
            String path = result.getCtTypePath();


            // map to hold the severities for this type
            Map<String, Integer> severities = new HashMap<>();
            // iterate over the same type and summarize severities if they exist or initialize them if not
            for (var result2 : results.stream().filter(Objects::nonNull)
                    .filter(de -> de.getCtType().equals(name) && de.getEnvironment().equals("ALL")
                    )
                    .toList()) {
                if (severities.containsKey(result2.getSeverity())) {
                    severities.put(result2.getSeverity(), severities.get(result2.getSeverity()) + result2.getTotal());
                } else {
                    severities.put(result2.getSeverity(), result2.getTotal());
                }
            }


            // initialize list that will be populated with EnvironmentSeverity objects for this type
            List<EnvironmentSeverity> environmentSeverities = new ArrayList<>();
            // populate List<EnvironmentSeverity> with not null results
            for (var envResult : results.stream().filter(Objects::nonNull)
                    .filter(de -> de.getCtType().equals(name) && !de.getEnvironment().equals("ALL")
                    )
                    .toList()) {
                String envName = envResult.getEnvironment();
                Map<String, Integer> envSeverities = new HashMap<>();
                // iterate over the same environment and summarize severities if they exist or initialize them if not
                for (var envResult2 : results.stream().filter(Objects::nonNull)
                        .filter(de -> de.getCtType().equals(name) && de.getEnvironment().equals(envName)
                        )
                        .toList()) {
                    if (envSeverities.containsKey(envResult2.getSeverity())) {
                        envSeverities.put(envResult2.getSeverity(), envSeverities.get(envResult2.getSeverity()) + envResult2.getTotal());
                    } else {
                        envSeverities.put(envResult2.getSeverity(), envResult2.getTotal());
                    }
                }
                // check if the environment has already been added to the list
                // if it has, add the count to the existing environment
                // if it hasn't, create a new environment and add it to the list
                if (environmentSeverities.stream().anyMatch(es -> es.getName().equals(envName))) {
                    environmentSeverities.stream().filter(es -> es.getName().equals(envName)).forEach(es -> es.getSeverities().putAll(envSeverities));
                } else {
                    environmentSeverities.add(new EnvironmentSeverity(envName, envSeverities));
                }
            }


            // check if the type has already been added to the list
            // if it has, add the severity map to the existing TypeSeverity object
            // if it hasn't, create a new TypeSeverity object and add it to the list
            if (typeSeverities.stream().anyMatch(ts -> ts.getName().equals(name))) {
                typeSeverities.stream().filter(ts -> ts.getName().equals(name)).forEach(ts -> ts.getSeverities().putAll(severities));
            } else {
                TypeAttendedSeverity typeSeverity = new TypeAttendedSeverity(name, path, severities, environmentSeverities);
                typeSeverities.add(typeSeverity);
            }
        }
        return typeSeverities;

    }

    @NotNull
    private List<EnvironmentSeverity> calculateAttendedEnvironmentSeverities(List<DashboardEntriesAttendedProjection> results) {
        // iterate through results and populate EnvironmentSeverity class
        List<EnvironmentSeverity> environmentSeverities = new ArrayList<>();
        for (var result : results.stream().filter(Objects::nonNull)
                .filter(de -> de.getCtType().equals("ALL") && !de.getEnvironment().equals("ALL") )
                .toList()) {
            String name = result.getEnvironment();
            // map to hold the severities for this environment
            Map<String, Integer> severities = new HashMap<>();
            // iterate over the same environment and summarize severities if they exist or initialize them if not
            for (var result2 : results.stream().filter(Objects::nonNull)
                    .filter(de -> de.getEnvironment().equals(name) && de.getCtType().equals("ALL") )
                    .toList()) {
                String severity = result2.getSeverity();
                int count = result2.getTotal();
                if (severities.containsKey(severity)) {
                    severities.put(severity, severities.get(severity) + count);
                } else {
                    severities.put(severity, count);
                }
            }
            // check if EnvironmentSeverity object is in environmentSeverities
            // if it is, add the severities to the existing object
            // if it is not, create a new EnvironmentSeverity object and add it to the list
            if (environmentSeverities.stream().anyMatch(es -> es.getName().equals(name))) {
                environmentSeverities.stream().filter(es -> es.getName().equals(name)).forEach(es -> es.getSeverities().putAll(severities));
            } else {
                EnvironmentSeverity environmentSeverity = new EnvironmentSeverity(name, severities);
                environmentSeverities.add(environmentSeverity);
            }


        }
        return environmentSeverities;
    }

    @NotNull
    private List<CategoryScore> calculateCategoryScores(List<DashboardEntriesProjection> results) {
        List<CategoryScore> categoryScores = new ArrayList<>();
        for (var result : results.stream().filter(Objects::nonNull)
                .filter(de -> de.getCtType().equals("ALL") && de.getEnvironment().equals("ALL") && !de.getMetricCategory().equals("ALL"))
                .toList()) {
            String name = result.getMetricCategory();
            Map<String, Integer> scores = new HashMap<>();
            for (var result2 : results.stream().filter(Objects::nonNull)
                    .filter(de -> de.getMetricCategory().equals(name) && de.getCtType().equals("ALL") && de.getEnvironment().equals("ALL"))
                    .toList()) {
                String score = result2.getScore();
                int count = result2.getTotal();
                if (scores.containsKey(score)) {
                    scores.put(score, scores.get(score) + count);
                } else {
                    scores.put(score, count);
                }
            }
            if (categoryScores.stream().anyMatch(cs -> cs.getName().equals(name))) {
                categoryScores.stream().filter(es -> es.getName().equals(name)).forEach(es -> es.getScores().putAll(scores));
            } else {
                categoryScores.add(new CategoryScore(name, scores));
            }

        }
        return categoryScores;
    }

}
