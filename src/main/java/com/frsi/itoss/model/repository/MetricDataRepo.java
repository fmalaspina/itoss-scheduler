package com.frsi.itoss.model.repository;

import com.frsi.itoss.model.continuousqueries.ContinuousQuery;
import org.springframework.stereotype.Component;

import java.sql.SQLException;

@Component
public interface MetricDataRepo {

    //void connect();

    //void close();

//    void save(MetricPayloadData mp) throws SQLException;


    Object query(String query) throws Exception;


    //List<QueryResult> queryWithChunkSize(String query, int chunkSize, TimeUnit timeunit, String databaseName);

    void deleteContinuousQuery(String queryName) throws SQLException;

    void createContinuousQuery(String query) throws SQLException;

    void createContinuousQuery(ContinuousQuery continuousQuery) throws SQLException;

    String getDatabaseName();

    void reset(Long metricId) throws Exception;

}