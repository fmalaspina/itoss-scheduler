package com.frsi.itoss.model.commonservices;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

@Service
public class TimeseriesDAOServices {


    private final HikariDataSource timeseriesdb;
    private final NamedParameterJdbcTemplate jdbcTemplate;

    public TimeseriesDAOServices(@Qualifier("timeseriesDataSource") HikariDataSource timeseriesdb) {
        this.timeseriesdb = timeseriesdb;
        this.jdbcTemplate = new NamedParameterJdbcTemplate(timeseriesdb);
    }


    public List<Map<String, Object>> query(String query) throws Exception {


        return queryForList(query);


    }

    public List<Map<String, Object>> queryForList(String query, Map<String, Object> params) throws Exception {


        MapSqlParameterSource mapParams = new MapSqlParameterSource();
        mapParams.addValues(params);
        return jdbcTemplate.queryForList(query, mapParams);


    }

    public void update(String query) throws Exception {


        jdbcTemplate.update(query, new MapSqlParameterSource());


    }


    public List<Map<String, Object>> queryForList(String query) throws Exception {


        MapSqlParameterSource mapParams = new MapSqlParameterSource();
        return jdbcTemplate.queryForList(query, mapParams);

    }

    public DatabaseMetaData getMetaData() throws SQLException {
        return this.timeseriesdb.getConnection().getMetaData();
    }

}

