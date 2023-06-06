package com.frsi.itoss.mgr.controllers;

import com.frsi.itoss.model.commonservices.ResultSetJSONConvertorService;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLException;

@RestController
@RequestMapping("/monitorCtStatuses/search")
public class MonitorCtStatusController {


    @Autowired
    ResultSetJSONConvertorService convertor;

    @RequestMapping(value = "/findByContainerPageable", method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity<?> findMonitorCtStatusByContainerPageable(@RequestParam("userId") Long userId,
                                                                    @RequestParam("containerId") Long containerId,
                                                                    @RequestParam("environment") String environment,
                                                                    @RequestParam("status") String status,
                                                                    @RequestParam("supportUserId") Long supportUserId,

                                                                    @RequestParam("workgroupIds") String workgroupIds, @RequestParam("locationIds") String locationIds,
                                                                    @RequestParam("companyIds") String companyIds,
                                                                    @RequestParam("ctIds") String ctIds,
                                                                    @RequestParam("page") Long page, @RequestParam("size") Long size, @RequestParam("sort") String sort) throws SQLException, Exception {
        return findBySpecificationPageable(userId, 0L, ctIds, "", 0L, environment, status, "", 0L, 0L, supportUserId, "", containerId.toString(), workgroupIds, locationIds, companyIds, page, size, sort);
    }


    @RequestMapping(value = "/findBySpecificationPageable", method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity<?> findBySpecificationPageable(@RequestParam("userId") Long userId,
                                                         @RequestParam("ctId") Long ctId,
                                                         @RequestParam("ctIds") String ctIds,
                                                         @RequestParam("ctName") String ctName,
                                                         @RequestParam("ctType") Long ctType,
                                                         @RequestParam("ctEnvironment") String ctEnvironment,
                                                         @RequestParam("status") String status,
                                                         @RequestParam("metricCategory") String metricCategory,
                                                         @RequestParam("monitorId") Long monitorId,
                                                         @RequestParam("companyId") Long companyId,
                                                         @RequestParam("supportUserId") Long supportUserId,
                                                         @RequestParam("monitorIds") String monitorIds,
                                                         @RequestParam("containerIds") String containerIds,
                                                         @RequestParam("workgroupIds") String workgroupIds,
                                                         @RequestParam("locationIds") String locationIds,
                                                         @RequestParam("companyIds") String companyIds,
                                                         @RequestParam("page") Long page,
                                                         @RequestParam("size") Long size,
                                                         @RequestParam("sort") String sort)

            throws SQLException, Exception {


        String select =
                "WITH tennant AS (SELECT cts_id FROM tennant_cts tc INNER JOIN tennant_users tu ON tu.tennant_id = tc.tennant_id WHERE tu.users_id = "
                        + userId
                        + ") SELECT coalesce(mcs.modified_at, mcs.created_at) as date, mcs.created_at, mo.name monitor, me.name metric, me.metric_category, c.name container, ct.id ct_id, ct.name ct_name, ct.attributes ct_attributes, ctt.name ct_type, ct.environment ct_environment,  co.name company, l.name AS location, mcs.status, mcs.error ";

        String selectCount = "WITH tennant AS (SELECT cts_id FROM tennant_cts tc INNER JOIN tennant_users tu ON tu.tennant_id = tc.tennant_id WHERE tu.users_id = " + userId + ") SELECT count(*) ";

        String selectStatusCount = "WITH tennant AS (SELECT cts_id FROM tennant_cts tc INNER JOIN tennant_users tu ON tu.tennant_id = tc.tennant_id WHERE tu.users_id = " + userId + ") SELECT count(*), mcs.status ";

        String groupByStatusCount = " GROUP BY mcs.status ";

        String from = "FROM monitor_ct_status mcs INNER JOIN monitor mo ON mcs.monitor_id = mo.id INNER JOIN container c ON c.id = mo.container_id INNER JOIN metric me ON me.id = mo.metric_id INNER JOIN ct ON ct.id = mcs.ct_id INNER JOIN ct_type ctt ON ctt.id = ct.type_id INNER JOIN company co ON co.id = ct.company_id INNER JOIN location l ON l.id = ct.location_id ";
        String where = " WHERE ct.id IN (SELECT cts_id FROM tennant) AND (" + ctId + " = 0 OR ct.id = " + ctId + ") AND (" + ctType + " = 0 OR ct.type_id = " + ctType + ") AND ('" + ctEnvironment + "' = '' OR ct.environment = '" + ctEnvironment + "') AND ('" + status + "' = '' OR mcs.status = '" + status + "') AND ('" + metricCategory + "' = '' OR me.metric_category = '" + metricCategory + "') AND (" + monitorId + " = 0 OR mo.id = " + monitorId + ") AND (" + supportUserId + " = 0 OR ct.support_user_id = " + supportUserId + ") AND  ('" + ctIds + "'" + " = '' OR (ct.id = ANY (select unnest(string_to_array('" + ctIds + "', ','))::bigint))) AND  ('" + containerIds + "' = '' OR c.id = ANY (select unnest(string_to_array('" + containerIds + "', ','))::bigint)) AND  ('" + monitorIds + "'" + " = '' OR mo.id = ANY (select unnest(string_to_array('" + monitorIds + "', ','))::bigint)) AND  (" + companyId + " = 0 OR co.id = " + companyId + ") AND ct.state = 'OPERATIONS' " +
                " AND ('" + workgroupIds + "'= '' OR ct.workgroup_id = ANY (select id from workgroups(string_to_array('" + workgroupIds + "', ',')::int[])))" +
                " AND ('" + locationIds + "'= '' OR ct.location_id = ANY (select id from locations(string_to_array('" + locationIds + "', ',')::int[])))" +
                " AND ('" + companyIds + "'= '' OR ct.company_id = ANY (SELECT unnest(string_to_array('" + companyIds + "', ','))::bigint))";
        String orderBy;
        if (sort != null && !sort.isBlank()) {
            String[] sortParts = sort.split(",");

            orderBy = " ORDER BY " + sortParts[0] + " " + (sortParts.length > 1 ? sortParts[1] : "") + ", 1 ASC";
        } else {
            orderBy = " ORDER BY mcs.modified_at DESC, 1 ASC";

        }

        String limit = " LIMIT " + size + " OFFSET " + page * size;

        String queryData = select + from + where + orderBy + limit;
        String queryCount = selectCount + from + where;
        String queryStatusCount = selectStatusCount + from + where + groupByStatusCount;

        JSONArray jaData = convertor.executeQuery(queryData);
        JSONArray jaCount = convertor.executeQuery(queryCount);
        JSONArray jaSeverity = convertor.executeQuery(queryStatusCount);


        JSONObject jaAll = new JSONObject();
        jaAll.put("page", jaCount.get(0));
        jaAll.put("data", jaData);
        jaAll.put("summary", jaSeverity);

        return ResponseEntity.ok(jaAll.toString());


    }


}
