package com.frsi.itoss.model.commonservices;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
public class ItossdbDAOServices {

    private final HikariDataSource itossdb;
    private final NamedParameterJdbcTemplate jdbcTemplate;

    public ItossdbDAOServices(@Qualifier("appDataSource") HikariDataSource itossdb) {

        this.itossdb = itossdb;
        this.jdbcTemplate = new NamedParameterJdbcTemplate(itossdb);
    }

    public List<Map<String, Object>> query(String query) throws Exception {


        return queryForList(query);


    }

    public List<Map<String, Object>> queryForList(String query, Map<String, Object> params) throws Exception {


        MapSqlParameterSource mapParams = new MapSqlParameterSource();
        mapParams.addValues(params);
        return jdbcTemplate.queryForList(query, mapParams);


    }

    public List<Map<String, Object>> queryForList(String query) throws Exception {


        MapSqlParameterSource mapParams = new MapSqlParameterSource();
        return jdbcTemplate.queryForList(query, mapParams);


    }

    public Map<String, Object> queryPageable(String queryData, String queryCount) throws Exception {


        MapSqlParameterSource mapParams = new MapSqlParameterSource();
        var result = new HashMap<String, Object>();
        result.put("data", jdbcTemplate.queryForList(queryData, mapParams));
        result.put("page", jdbcTemplate.queryForMap(queryCount, mapParams));
        return result;


    }
}



