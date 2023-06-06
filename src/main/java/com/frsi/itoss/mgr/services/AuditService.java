package com.frsi.itoss.mgr.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AuditService {
    @Autowired
    NamedParameterJdbcTemplate appJdbcTemplate;

    @Value("${spring.jpa.properties.org.hibernate.envers.default_schema}")
    String schema;

    public Map<String, Object> getRevisionsBetweenDates(String entityName, int revType, Long id,
                                                              String user, String dateFrom, String dateTo, int size, int page) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("revType", revType);
        params.addValue("id", id);
        params.addValue("dateFrom", dateFrom);
        params.addValue("dateTo", dateTo);
        params.addValue("userName", user);

        String sqlFrom = " from " + schema + "." + entityName + "_aud r ";

        String sqlWhere = """ 
                where (:revType = -1 or revtype = :revType)
                and (:id = 0 or id = :id)
                and (:userName = '' or last_modified_by = :userName) 
                """;

        if (!dateFrom.isBlank()) {
            sqlWhere += " and last_modified_date >= TO_TIMESTAMP(:dateFrom,'YYYY-MM-DD HH24:MI:ss')";
        }
        if (!dateTo.isBlank()) {
            sqlWhere += " and last_modified_date <= TO_TIMESTAMP(:dateTo,'YYYY-MM-DD HH24:MI:ss')";
        }
        String sql = "select r.*, case r.revtype when 0 then 'ADD' when 1 then 'MOD' when 2 then 'DEL' end as revision_operation " +
                sqlFrom + sqlWhere + " ORDER BY r.last_modified_date LIMIT " + size + " OFFSET " + page * size;
        String sqlCount = "select count(*) as total " +
                sqlFrom +
                sqlWhere;

        var result =  appJdbcTemplate.queryForList(sql, params);
        var paging =  appJdbcTemplate.queryForList(sqlCount, params);
        var map = new HashMap<String,Object>();
        map.put("result", result);
        map.put("page", page);
        map.put("size",size);
        map.put("total", paging.get(0).get("total"));
        return map;
    }


    public List<Map<String, Object>> getRevision(String entityName, Long id, Number revId) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("id", id);
        params.addValue("revId", revId);
        return appJdbcTemplate.queryForList(
                "select r.*, case r.revtype when 0 then 'ADD' when 1 then 'MOD' when 2 then 'DEL' end as revision_operation from " +
                schema + "." +
                entityName +
                "_aud r where id = :id" +
                " and rev = :revId"
                        ,params);
    }


    public List<Map<String, Object>> getRevisionsDifferences(String ct, long id, int rev1, int rev2) {
        var revision1 = getRevision(ct, id, rev1);
        var revision2 = getRevision(ct, id, rev2);
        var diff = revisionCompare(revision1, revision2);
        return diff;
    }


    private List<Map<String, Object>> revisionCompare(List<Map<String, Object>> revision1, List<Map<String, Object>> revision2) {
        List<Map<String, Object>> differences = new ArrayList<>();
        // Iterate over the maps in revision1
        for (Map<String, Object> map1 : revision1) {
            // Check if a corresponding map exists in revision2
            boolean found = false;
            for (Map<String, Object> map2 : revision2) {
                if (map1.keySet().equals(map2.keySet())) {
                    found = true;
                    // Compare the values for each key
                    for (String key : map1.keySet()) {
                        Object value1 = map1.get(key);
                        Object value2 = map2.get(key);
                        if (value1 == null && value2 == null) {
                            // Both values are null, so they are considered equal
                            continue;
                        }
                        if (value1 == null || value2 == null || !value1.equals(value2)) {
                            // Add the difference to the resulting list
                            Map<String, Object> differenceMap = new HashMap<>();
                            differenceMap.put("key", key);
                            differenceMap.put("value1", value1);
                            differenceMap.put("value2", value2);
                            differences.add(differenceMap);
                        }
                    }
                    break;
                }
            }
            // If no corresponding map is found in revision2, add all key-value pairs from map1 as differences
            if (!found) {
                Map<String, Object> differenceMap = new HashMap<>();
                for (String key : map1.keySet()) {
                    differenceMap.put("key", key);
                    differenceMap.put("value1", map1.get(key));
                    differenceMap.put("value2", null); // Indicate that value2 is missing
                    differences.add(differenceMap);
                }
            }
        }
        // Iterate over the maps in revision2 and add any additional maps that don't exist in revision1 as differences
        for (Map<String, Object> map2 : revision2) {
            boolean found = false;
            for (Map<String, Object> map1 : revision1) {
                if (map2.keySet().equals(map1.keySet())) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                Map<String, Object> differenceMap = new HashMap<>();
                for (String key : map2.keySet()) {
                    differenceMap.put("key", key);
                    differenceMap.put("value1", null); // Indicate that value1 is missing
                    differenceMap.put("value2", map2.get(key));
                    differences.add(differenceMap);
                }
            }
        }
        return differences;
    }

}




