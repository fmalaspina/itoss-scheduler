package com.frsi.itoss.model.repository;//package com.frsi.itoss.model.repository;
//
//import com.frsi.itoss.model.continuousqueries.ContinuousQuery;
//import com.frsi.itoss.model.profile.Metric;
//import com.frsi.itoss.shared.MetricPayloadData;
//import lombok.extern.java.Log;
//import org.influxdb.BatchOptions;
//import org.influxdb.InfluxDB;
//import org.influxdb.InfluxDBFactory;
//import org.influxdb.dto.Point;
//import org.influxdb.dto.Query;
//import org.influxdb.dto.QueryResult;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
//import org.springframework.stereotype.Repository;
//
//import java.sql.SQLException;
//import java.util.Collections;
//import java.util.LinkedList;
//import java.util.List;
//import java.util.Optional;
//import java.util.concurrent.CountDownLatch;
//import java.util.concurrent.TimeUnit;
//
//@Repository
//@Log
//@ConditionalOnProperty(
//        value = "itoss.timeseries.database",
//        havingValue = "influx",
//        matchIfMissing = true)
//public class InfluxMetricDataRepoImpl implements MetricDataRepo {
//
//    //	@Autowired
////	MonitorRepo monitorRepo;
//    @Value("${metrics.influxdb.url}")
//    private String url;
//    @Value("${metrics.influxdb.database}")
//    private String database;
//    @Value("${metrics.influxdb.username}")
//    private String username;
//    @Value("${metrics.influxdb.password}")
//    private String password;
//    private InfluxDB influxDB;
//
//    @Autowired
//    private MetricRepo metricRepo;
//
//    public InfluxMetricDataRepoImpl() {
//
//    }
//
//
//    public void connect() {
//
//        this.influxDB = InfluxDBFactory.connect(url, username, password);
//        this.influxDB.setDatabase(database);
//        if (!this.influxDB.query(new Query("SHOW DATABASES")).toString().contains(database)) {
//            this.influxDB.query(new Query("CREATE DATABASE " + database + " WITH DURATION 60d", database));
//
//            // Flush every 2000 Points, at least every 100ms
//            influxDB.enableBatch(BatchOptions.DEFAULTS.actions(2000).flushDuration(100));
//            this.influxDB.setDatabase(database);
//
//        }
//
//    }
//
//
//    public void close() {
//        this.influxDB.close();
//    }
//
//    @Override
//    public void save(MetricPayloadData mp) {
//        connect();
//        // Map<String, Object> convertedMap = new HashMap<String, Object>();
//
//        // convertedMap.putAll(mp.getFields());
//
//        final Point p = Point.measurement(mp.getMetricName()).time(System.currentTimeMillis(), TimeUnit.MILLISECONDS)
//                .tag(mp.getTagsAsStrings()).fields(mp.getFields()).build();
//        this.influxDB.write(p);
//
//        close();
//    }
//
//    @Override
//    public QueryResult query(String query) {
//        connect();
//        QueryResult result = this.influxDB.query(new Query(query, database));
//        close();
//        return result;
//    }
//
//
//    public List<QueryResult> queryWithChunkSize(String query, int chunkSize, TimeUnit timeunit, String databaseName) {
//
//        final CountDownLatch latch = new CountDownLatch(1);
//        connect();
//        Query influxQuery = new Query(query, databaseName);
//        if (chunkSize > 0) {
//            List<QueryResult> results = new LinkedList<>();
//            influxDB.query(influxQuery, chunkSize, result -> {
//                if (isQueryDone(result.getError())) {
//                    latch.countDown();
//                } else {
//                    results.add(result);
//                }
//            });
//            try {
//                latch.await();
//            } catch (InterruptedException e) {
//                // TODO Auto-generated catch block
//                e.printStackTrace();
//            }
//            close();
//            return results;
//        } else {
//
//            List<QueryResult> results = Collections.singletonList(influxDB.query(influxQuery, timeunit));
//            close();
//            return results;
//        }
//
//
//    }
//
//
//    @Override
//    public void createContinuousQuery(ContinuousQuery continuousQuery) {
//
//
//        String database = continuousQuery.getDatabase();
//        String query = new String("CREATE CONTINUOUS QUERY " + continuousQuery.getName() + " ON "
//                + database + " BEGIN  SELECT " + continuousQuery.getFormatedFunctionsForInflux() + " INTO " +
//                database + "." + continuousQuery.getBucket() + "." + continuousQuery.getName() + " FROM " + continuousQuery.getMetric().getName() + " GROUP BY time(" + continuousQuery.getGranularity() + "), * END");
//        this.query(query);
//        log.info("QUERY " + query);
//    }
//
//    private boolean isQueryDone(String error) {
//        if (error != null) {
//            error.equals("DONE");
//            return true;
//        }
//        return false;
//    }
//
//    @Override
//    public void deleteContinuousQuery(String queryName) {
//        this.query("DROP CONTINUOUS QUERY " + queryName + " ON " + database);
//
//    }
//
//    @Override
//    public void createContinuousQuery(String query) {
//        this.query(query);
//
//    }
//
//    @Override
//    public String getDatabaseName() {
//
//        return this.database;
//    }
//
//    @Override
//    public void reset(Long metricId) throws SQLException {
//        Optional<Metric> metric = metricRepo.findById(metricId);
//        try {
//            if (metric.isPresent()) {
//                query("DROP MEASUREMENT " + metric.get().getName().toLowerCase());
//            }
//        } catch (Exception e) {
//            throw new SQLException("Could not reset metric data for metric " + metric.get().getName().toLowerCase());
//        }
//    }
//
//}
