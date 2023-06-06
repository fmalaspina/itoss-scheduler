package com.frsi.itoss.mgr.controllers;

import com.frsi.itoss.mgr.services.CtStatusService;
import com.frsi.itoss.model.commonservices.ItossdbDAOServices;
import com.frsi.itoss.model.commonservices.ResultSetJSONConvertorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/ctStatuses/search")
public class CtStatusController {

    @Autowired
    ResultSetJSONConvertorService convertor;
    @Autowired
    ItossdbDAOServices itossdbDAOServices;
    @Autowired
    CtStatusService ctStatusService;

    @RequestMapping(value = "/counter", method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity<?> findCtStatusCount(@RequestParam("userId") Long userId, @RequestParam("environment") String environment, @RequestParam("supportUserId") Long supportUserId) throws SQLException, Exception {
        String query =
                "WITH tennant AS (SELECT cts_id FROM tennant_cts tc INNER JOIN tennant_users tu ON tu.tennant_id = tc.tennant_id WHERE tu.users_id = + " + userId + ")" +
                        " SELECT co.id company_id, co.name company_name, ctt.id type_id, ctt.name type_name, ctt.type_path, cts.down, count(*) quantity FROM ct ct INNER JOIN ct_type ctt on ctt.id = ct.type_id INNER JOIN ct_status cts on cts.id = ct.id   INNER JOIN company co on co.id = ct.company_id WHERE ct.id IN (SELECT cts_id FROM tennant) AND ('" + environment + "' = '' OR ct.environment = ANY (select unnest(string_to_array('" + environment + "', ',')))) AND (" + supportUserId + " = 0 OR ct.support_user_id = " + supportUserId + ") AND ct.state = 'OPERATIONS' GROUP BY co.id, co.name, ctt.id, ctt.name, ctt.type_path, cts.down ORDER BY co.name,ctt.name";
        return convertor.execute(query);


    }

    @RequestMapping(value = "/counterByLocation", method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity<?> findCtStatusCountByLocation(@RequestParam("userId") Long userId, @RequestParam("environment") String environment, @RequestParam("supportUserId") Long supportUserId) throws SQLException, Exception {

        String query =
                "WITH tennant AS (SELECT cts_id FROM tennant_cts tc INNER JOIN tennant_users tu ON tu.tennant_id = tc.tennant_id WHERE tu.users_id = + " + userId + ")" +
                        " SELECT lo.id location_id, lo.name location_name, ctt.id type_id, ctt.name type_name, ctt.type_path, cts.down, count(*) quantity FROM ct ct INNER JOIN ct_type ctt on ctt.id = ct.type_id INNER JOIN ct_status cts on cts.id = ct.id   INNER JOIN location lo on lo.id = ct.location_id WHERE ct.id IN (SELECT cts_id FROM tennant) AND ('" + environment + "' = '' OR ct.environment = ANY (select unnest(string_to_array('" + environment + "', ',')))) AND (" + supportUserId + " = 0 OR ct.support_user_id = " + supportUserId + ") AND ct.state = 'OPERATIONS' GROUP BY lo.id, lo.name, ctt.id, ctt.name, ctt.type_path, cts.down ORDER BY lo.name,ctt.name";
        return convertor.execute(query);


    }

    @RequestMapping(value = "/counterByLocationTree", method = RequestMethod.GET, produces = "application/json")
    public List<Map<String, Object>> findCtStatusCountByLocationTree(@RequestParam("userId") Long userId, @RequestParam("environment") String environment, @RequestParam("supportUserId") Long supportUserId) throws SQLException, Exception {
        return ctStatusService.getCTStatusTree(userId, environment, supportUserId);
    }

    @RequestMapping(value = "/counterByCompanyAndLocation", method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity<?> findCtStatusCountByCompanyAndLocation(@RequestParam("userId") Long userId, @RequestParam("environment") String environment, @RequestParam("supportUserId") Long supportUserId) throws SQLException, Exception {

        String query =
                "WITH tennant AS (SELECT cts_id FROM tennant_cts tc INNER JOIN tennant_users tu ON tu.tennant_id = tc.tennant_id WHERE tu.users_id = + " + userId + ")" +
                        " SELECT lo.id location_id, lo.name location_name, co.id company_id, co.name company_name ,ctt.id type_id, ctt.name type_name, ctt.type_path, cts.down, count(*) quantity FROM ct ct INNER JOIN ct_type ctt on ctt.id = ct.type_id INNER JOIN ct_status cts on cts.id = ct.id  INNER JOIN company co on co.id = ct.company_id  INNER JOIN location lo on lo.id = ct.location_id WHERE ct.id IN (SELECT cts_id FROM tennant) AND ('" + environment + "' = '' OR ct.environment = ANY (select unnest(string_to_array('" + environment + "', ',')))) AND (" + supportUserId + " = 0 OR ct.support_user_id = " + supportUserId + ") AND ct.state = 'OPERATIONS' GROUP BY lo.id, lo.name, co.id, co.name,ctt.id, ctt.name, ctt.type_path, cts.down ORDER BY lo.name, co.name,ctt.name";
        return convertor.execute(query);


    }


    @RequestMapping(value = "/findByCompanyAndDown", method = RequestMethod.GET, produces = "application/json")
    public List<?> findByCompanyAndDown(@RequestParam("userId") Long userId, @RequestParam("companyId") Long companyId, @RequestParam("down") boolean down, @RequestParam("environment") String environment, @RequestParam("supportUserId") Long supportUserId) throws SQLException, Exception {
        String query =
                "WITH tennant AS (SELECT cts_id FROM tennant_cts tc INNER JOIN tennant_users tu ON tu.tennant_id = tc.tennant_id WHERE tu.users_id = " + userId +
                        ") SELECT ct.id, ct.name, ct.attributes, ct.environment, co.name AS company_name, ctt.name AS type_name, COALESCE(cts.last_status_change, cts.created_at) AS date  FROM ct ct INNER JOIN ct_type ctt on ctt.id = ct.type_id INNER JOIN ct_status cts on cts.id = ct.id INNER JOIN company co on co.id = ct.company_id WHERE ct.id IN (SELECT cts_id FROM tennant) AND co.id = " + companyId + "  AND cts.down = " + down + " AND ('" + environment + "' = '' OR ct.environment = ANY (select unnest(string_to_array('" + environment + "', ',')))) AND (" + supportUserId + " = 0 OR ct.support_user_id = " + supportUserId + ") AND ct.state = 'OPERATIONS' ORDER BY co.name, ctt.name";
        return itossdbDAOServices.query(query);

    }

    @RequestMapping(value = "/findByCompanyAndDownPageable", method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity<?> findByCompanyAndDownPageable(@RequestParam("userId") Long userId, @RequestParam("companyId") Long companyId, @RequestParam("down") boolean down, @RequestParam("environment") String environment, @RequestParam("supportUserId") Long supportUserId, @RequestParam("page") Long page, @RequestParam("size") Long size, @RequestParam("sort") String sort) throws SQLException, Exception {
        String queryData =
                "WITH tennant AS (SELECT cts_id FROM tennant_cts tc INNER JOIN tennant_users tu ON tu.tennant_id = tc.tennant_id WHERE tu.users_id = " + userId +
                        ") SELECT ct.id, ct.name, ct.attributes, ct.environment, co.name AS company_name, ctt.name AS type_name, COALESCE(cts.last_status_change, cts.created_at) AS date  FROM ct ct INNER JOIN ct_type ctt on ctt.id = ct.type_id INNER JOIN ct_status cts on cts.id = ct.id INNER JOIN company co on co.id = ct.company_id WHERE ct.id IN (SELECT cts_id FROM tennant) AND co.id = " + companyId + "  AND cts.down = " + down + " AND ('" + environment + "' = '' OR ct.environment = ANY (select unnest(string_to_array('" + environment + "', ',')))) AND (" + supportUserId + " = 0 OR ct.support_user_id = " + supportUserId + ") AND ct.state = 'OPERATIONS'";

        String orderBy;
        if (sort != null && !sort.isBlank()) {
            String[] sortParts = sort.split(",");

            orderBy = " ORDER BY " + sortParts[0] + " " + (sortParts.length > 1 ? sortParts[1] : "") + ", 1 ASC";
        } else {
            orderBy = "  ORDER BY co.name, ctt.name";

        }

        String limit = " LIMIT " + size + " OFFSET " + page * size;

        queryData = queryData + orderBy + limit;

        String queryCount =
                "WITH tennant AS (SELECT cts_id FROM tennant_cts tc INNER JOIN tennant_users tu ON tu.tennant_id = tc.tennant_id WHERE tu.users_id = " + userId +
                        ") SELECT count(*)  FROM ct ct INNER JOIN ct_type ctt on ctt.id = ct.type_id INNER JOIN ct_status cts on cts.id = ct.id INNER JOIN company co on co.id = ct.company_id WHERE ct.id IN (SELECT cts_id FROM tennant) AND co.id = " + companyId + "  AND cts.down = " + down + " AND ('" + environment + "' = '' OR ct.environment = ANY (select unnest(string_to_array('" + environment + "', ',')))) AND (" + supportUserId + " = 0 OR ct.support_user_id = " + supportUserId + ") AND ct.state = 'OPERATIONS'";
        return convertor.executePageable(queryData, queryCount);

    }

    @RequestMapping(value = "/findByLocationAndCompanyAndDownPageable", method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity<?> findByLocationAndCompanyAndDownPageable(@RequestParam("locationId") Long locationId, @RequestParam("userId") Long userId, @RequestParam("companyId") Long companyId, @RequestParam("down") boolean down, @RequestParam("environment") String environment, @RequestParam("supportUserId") Long supportUserId, @RequestParam("page") Long page, @RequestParam("size") Long size, @RequestParam("sort") String sort) throws SQLException, Exception {
        String queryData =
                "WITH tennant AS (SELECT cts_id FROM tennant_cts tc INNER JOIN tennant_users tu ON tu.tennant_id = tc.tennant_id WHERE tu.users_id = " + userId +
                        ") SELECT ct.id, ct.name, ct.attributes, ct.environment, co.name AS company_name, ctt.name AS type_name, COALESCE(cts.last_status_change, cts.created_at) AS date  FROM ct ct INNER JOIN ct_type ctt on ctt.id = ct.type_id INNER JOIN ct_status cts on cts.id = ct.id INNER JOIN company co on co.id = ct.company_id WHERE ct.id IN (SELECT cts_id FROM tennant) AND ct.location_id = " + locationId + " AND co.id = " + companyId + "  AND cts.down = " + down + " AND ('" + environment + "' = '' OR ct.environment = ANY (select unnest(string_to_array('" + environment + "', ',')))) AND (" + supportUserId + " = 0 OR ct.support_user_id = " + supportUserId + ") AND ct.state = 'OPERATIONS'";

        String orderBy;
        if (sort != null && !sort.isBlank()) {
            String[] sortParts = sort.split(",");

            orderBy = " ORDER BY " + sortParts[0] + " " + (sortParts.length > 1 ? sortParts[1] : "") + ", 1 ASC";
        } else {
            orderBy = "  ORDER BY co.name, ctt.name";

        }

        String limit = " LIMIT " + size + " OFFSET " + page * size;

        queryData = queryData + orderBy + limit;

        String queryCount =
                "WITH tennant AS (SELECT cts_id FROM tennant_cts tc INNER JOIN tennant_users tu ON tu.tennant_id = tc.tennant_id WHERE tu.users_id = " + userId +
                        ") SELECT count(*)  FROM ct ct INNER JOIN ct_type ctt on ctt.id = ct.type_id INNER JOIN ct_status cts on cts.id = ct.id INNER JOIN company co on co.id = ct.company_id WHERE ct.id IN (SELECT cts_id FROM tennant) AND co.id = " + companyId + "  AND cts.down = " + down + " AND ('" + environment + "' = '' OR ct.environment = ANY (select unnest(string_to_array('" + environment + "', ',')))) AND (" + supportUserId + " = 0 OR ct.support_user_id = " + supportUserId + ") AND ct.state = 'OPERATIONS'";
        return convertor.executePageable(queryData, queryCount);

    }


    @RequestMapping(value = "/findByLocationAndDownPageable", method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity<?> findByLocationAndDownPageable(@RequestParam("userId") Long userId, @RequestParam("locationId") Long locationId, @RequestParam("down") boolean down, @RequestParam("environment") String environment, @RequestParam("supportUserId") Long supportUserId, @RequestParam("page") Long page, @RequestParam("size") Long size, @RequestParam("sort") String sort) throws SQLException, Exception {
        String queryData =
                "WITH tennant AS (SELECT cts_id FROM tennant_cts tc INNER JOIN tennant_users tu ON tu.tennant_id = tc.tennant_id WHERE tu.users_id = " + userId +
                        ") SELECT ct.id, ct.name, ct.attributes, ct.environment, co.name AS company_name, ctt.name AS type_name, COALESCE(cts.last_status_change, cts.created_at) AS date  FROM ct ct INNER JOIN ct_type ctt on ctt.id = ct.type_id INNER JOIN ct_status cts on cts.id = ct.id INNER JOIN company co on co.id = ct.company_id WHERE ct.id IN (SELECT cts_id FROM tennant) " +
                        " AND ct.location_id IN ( select id from locations(string_to_array('" + locationId + "', ',')::int[]) ) AND cts.down = " + down + " AND ('" + environment + "' = '' OR ct.environment = ANY (select unnest(string_to_array('" + environment + "', ',')))) AND (" + supportUserId + " = 0 OR ct.support_user_id = " + supportUserId + ") AND ct.state = 'OPERATIONS'";

        String orderBy;
        if (sort != null && !sort.isBlank()) {
            String[] sortParts = sort.split(",");

            orderBy = " ORDER BY " + sortParts[0] + " " + (sortParts.length > 1 ? sortParts[1] : "") + ", 1 ASC";
        } else {
            orderBy = "  ORDER BY co.name, ctt.name";

        }

        String limit = " LIMIT " + size + " OFFSET " + page * size;

        queryData = queryData + orderBy + limit;

        String queryCount =
                "WITH tennant AS (SELECT cts_id FROM tennant_cts tc INNER JOIN tennant_users tu ON tu.tennant_id = tc.tennant_id WHERE tu.users_id = " + userId +
                        ") SELECT count(*)  FROM ct ct INNER JOIN ct_type ctt on ctt.id = ct.type_id INNER JOIN ct_status cts on cts.id = ct.id INNER JOIN company co on co.id = ct.company_id WHERE ct.id IN (SELECT cts_id FROM tennant) AND ct.location_id IN ( select id from locations(string_to_array('" + locationId + "', ',')::int[])) AND cts.down = " + down + " AND ('" + environment + "' = '' OR ct.environment = ANY (select unnest(string_to_array('" + environment + "', ',')))) AND (" + supportUserId + " = 0 OR ct.support_user_id = " + supportUserId + ") AND ct.state = 'OPERATIONS'";
        return convertor.executePageable(queryData, queryCount);

    }

    @RequestMapping(value = "/findByCtTypeAndDownPageable", method = RequestMethod.GET, produces = "application/json")
    public Map<String, Object> findByCtTypeAndDownPageable(@RequestParam("userId") Long userId,
                                                           @RequestParam("typeId") Long typeId,
                                                           @RequestParam("down") boolean down,
                                                           @RequestParam("environment") String environment,
                                                           @RequestParam("supportUserId") Long supportUserId,
                                                           @RequestParam("page") Long page,
                                                           @RequestParam("size") Long size,
                                                           @RequestParam("sort") String sort) throws SQLException, Exception {
        String queryData =
                "WITH tennant AS (SELECT cts_id FROM tennant_cts tc INNER JOIN tennant_users tu ON tu.tennant_id = tc.tennant_id WHERE tu.users_id = " + userId +
                        ") SELECT ct.id, ct.name, ct.attributes\\:\\:text, ct.environment, co.name AS company_name, ctt.name AS type_name, COALESCE(cts.last_status_change, cts.created_at) AS date  FROM ct ct INNER JOIN ct_type ctt on ctt.id = ct.type_id INNER JOIN ct_status cts on cts.id = ct.id INNER JOIN company co on co.id = ct.company_id WHERE ct.id IN (SELECT cts_id FROM tennant) AND ct.type_id = " + typeId + "  AND cts.down = " + down + " AND ('" + environment + "' = '' OR ct.environment = ANY (select unnest(string_to_array('" + environment + "', ',')))) AND (" + supportUserId + " = 0 OR ct.support_user_id = " + supportUserId + ") AND ct.state = 'OPERATIONS'";

        String orderBy;
        if (sort != null && !sort.isBlank()) {
            String[] sortParts = sort.split(",");

            orderBy = " ORDER BY " + sortParts[0] + " " + (sortParts.length > 1 ? sortParts[1] : "") + ", 1 ASC";
        } else {
            orderBy = "  ORDER BY co.name, ctt.name";

        }

        String limit = " LIMIT " + size + " OFFSET " + page * size;

        queryData = queryData + orderBy + limit;

        String queryCount =
                "WITH tennant AS (SELECT cts_id FROM tennant_cts tc INNER JOIN tennant_users tu ON tu.tennant_id = tc.tennant_id WHERE tu.users_id = " + userId +
                        ") SELECT count(DISTINCT ct.id)  FROM ct ct INNER JOIN ct_type ctt on ctt.id = ct.type_id INNER JOIN ct_status cts on cts.id = ct.id INNER JOIN company co on co.id = ct.company_id WHERE ct.id IN (SELECT cts_id FROM tennant) AND ct.type_id = " + typeId + "  AND cts.down = " + down + " AND ('" + environment + "' = '' OR ct.environment = ANY (select unnest(string_to_array('" + environment + "', ',')))) AND (" + supportUserId + " = 0 OR ct.support_user_id = " + supportUserId + ") AND ct.state = 'OPERATIONS'";
        return itossdbDAOServices.queryPageable(queryData, queryCount);

    }


}
