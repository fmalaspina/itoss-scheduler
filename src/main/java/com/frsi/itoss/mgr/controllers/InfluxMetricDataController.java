//package com.frsi.itoss.mgr.controllers;
//
//import com.frsi.itoss.model.continuousqueries.MetricDefaults;
//import com.frsi.itoss.model.repository.MetricDataRepo;
//import org.influxdb.dto.QueryResult;
//import org.influxdb.dto.QueryResult.Result;
//import org.influxdb.dto.QueryResult.Series;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
//import org.springframework.http.ResponseEntity;
//import org.springframework.stereotype.Controller;
//import org.springframework.web.bind.annotation.*;
//
//import javax.ws.rs.QueryParam;
//import java.util.ArrayList;
//import java.util.List;
//
//@Controller
//@RequestMapping("metricData")
//@ConditionalOnProperty(
//        value = "itoss.timeseries.database",
//        havingValue = "influx",
//        matchIfMissing = true)
//public class InfluxMetricDataController {
//    @Autowired
//    MetricDataRepo metricDataRepo;
//    @Autowired
//    MetricDefaults metricDefaults;
//    @Value("${metrics.influxdb.database}")
//    private String database;
//
////	@PostMapping(value = "/query", produces = "application/json")
////	public ResponseEntity<?> queryMetricDataWithPostBody(@RequestBody QueryString query) {
////
////		return ResponseEntity.ok(metricDataRepo.query(query.getQuery()));
////	}
//
//    @PostMapping(value = "/{id}/reset", produces = "application/json")
//    public ResponseEntity<?> resetMetricDataWithParam(@PathVariable(required = true) Long id) throws Exception {
//        metricDataRepo.reset(id);
//        return ResponseEntity.ok(null);
//    }
//
//
//    @PostMapping(value = "/query", produces = "application/json")
//    public ResponseEntity<?> queryMetricDataWithPostParam(@RequestParam QueryString q) throws Exception {
//
//        return ResponseEntity.ok(metricDataRepo.query(q.getQuery()));
//    }
//
//    @GetMapping(value = "/query", produces = "application/json")
//    public ResponseEntity<?> queryMetricDataWithGetParam(@RequestParam QueryString q) throws Exception {
//
//        return ResponseEntity.ok(metricDataRepo.query(q.getQuery()));
//    }
//
//    @GetMapping(value = "/lastMetricByCt", produces = "application/json")
//    public ResponseEntity<?> lastMetricByCt(@RequestParam(value = "ctId") Long ctId,
//                                                 @RequestParam(value = "metricName") String metricName, @RequestParam(value = "tz") String tz) throws Exception {
//
//        StringBuilder sb = new StringBuilder();
//
//        sb.append("SELECT * FROM ");
//        sb.append("\"" + metricName + "\"");
//        sb.append(" WHERE ctId='");
//        sb.append(ctId);
//        sb.append("' group by * ORDER BY time DESC LIMIT 1");
//        if (tz != null) {
//            sb.append(" ");
//            sb.append("tz('");
//            sb.append(tz);
//            sb.append("')");
//        }
//        return ResponseEntity.ok(metricDataRepo.query(sb.toString()));
//    }
//
//    @GetMapping(value = "/metricByCtLastHour", produces = "application/json")
//    public ResponseEntity<?> queryMetricDataByCtLastHour(@RequestParam(value = "ctId") Long ctId,
//                                                         @RequestParam(value = "metricName") String metricName, @RequestParam(value = "tz") String tz) throws Exception {
//
//        StringBuilder sb = new StringBuilder();
//
//        sb.append("SELECT * FROM ");
//        sb.append("\"" + metricName + "\"");
//        sb.append(" WHERE ctId='");
//        sb.append(ctId);
//        sb.append("' AND time > now() - 1h group by * ORDER BY time  ");
//        if (tz != null) {
//            sb.append(" ");
//            sb.append("tz('");
//            sb.append(tz);
//            sb.append("')");
//        }
//        return ResponseEntity.ok(metricDataRepo.query(sb.toString()));
//    }
//
//    @GetMapping(value = "/metricByCtLastDay", produces = "application/json")
//    public ResponseEntity<?> queryMetricDataByCtLastDay(@RequestParam(value = "ctId") Long ctId,
//                                                        @RequestParam(value = "metricName") String metricName, @RequestParam(value = "tz") String tz) throws Exception {
//
//        StringBuilder sb = new StringBuilder();
//
//        sb.append("SELECT * FROM ");
//        sb.append("\"" + metricName + "\"");
//        sb.append(" WHERE ctId='");
//        sb.append(ctId);
//        sb.append("' AND time > now() - 1d group by * ORDER BY time  ");
//        if (tz != null) {
//            sb.append(" ");
//            sb.append("tz('");
//            sb.append(tz);
//            sb.append("')");
//        }
//        return ResponseEntity.ok(metricDataRepo.query(sb.toString()));
//    }
//
//    @GetMapping(value = "/metricByCtLastWeek", produces = "application/json")
//    public ResponseEntity<?> queryMetricDataByCtLastWeek(@RequestParam(value = "ctId") Long ctId,
//                                                         @RequestParam(value = "metricName") String metricName, @RequestParam(value = "tz") String tz) throws Exception {
//
//        StringBuilder sb = new StringBuilder();
//
//        sb.append("SELECT * FROM ");
//        sb.append("\"" + metricName + "\"");
//        sb.append(" WHERE ctId='");
//        sb.append(ctId);
//        sb.append("' AND time > now() - 1w group by * ORDER BY time  ");
//        if (tz != null) {
//            sb.append(" ");
//            sb.append("tz('");
//            sb.append(tz);
//            sb.append("')");
//        }
//        return ResponseEntity.ok(metricDataRepo.query(sb.toString()));
//    }
//
//    @GetMapping(value = "/metricByCtLastMonth", produces = "application/json")
//    public ResponseEntity<?> queryMetricDataByCtLastMonth(@RequestParam(value = "ctId") Long ctId,
//                                                          @RequestParam(value = "metricName") String metricName, @RequestParam(value = "tz") String tz) throws Exception {
//
//        StringBuilder sb = new StringBuilder();
//
//        sb.append("SELECT * FROM ");
//        sb.append("\"" + metricName + "\"");
//        sb.append(" WHERE ctId='");
//        sb.append(ctId);
//        sb.append("' AND time > now() - 30d group by * ORDER BY time  ");
//        if (tz != null) {
//            sb.append(" ");
//            sb.append("tz('");
//            sb.append(tz);
//            sb.append("')");
//        }
//        return ResponseEntity.ok(metricDataRepo.query(sb.toString()));
//    }
//
//    @GetMapping(value = "/metricByCtBetweenDates", produces = "application/json")
//    public ResponseEntity<?> lastMetricByCt(@RequestParam(value = "ctId") Long ctId,
//                                                 @RequestParam(value = "metricName") String metricName, @RequestParam(value = "from") String from,
//                                                 @RequestParam(value = "to") String to, @RequestParam(value = "tz") String tz) throws Exception {
//
//        StringBuilder sb = new StringBuilder();
//
//        sb.append("SELECT * FROM ");
//        sb.append("\"" + metricName + "\"");
//        sb.append(" WHERE ctId='");
//        sb.append(ctId);
//        sb.append("' AND time >= '");
//        sb.append(from);
//        sb.append("' AND time <= '");
//        sb.append(to);
//        sb.append("' group by * ORDER BY time ASC");
//        if (tz != null) {
//            sb.append(" ");
//            sb.append("tz('");
//            sb.append(tz);
//            sb.append("')");
//        }
//        return ResponseEntity.ok(metricDataRepo.query(sb.toString()));
//    }
//
//    @GetMapping(value = "/continuousQueries/list", produces = "application/json")
//    public ResponseEntity<?> listContinuousQueriesByMetric(@RequestParam(value = "metricName") String metricName) throws Exception {
//
//        List<ContinuousQueryAdapter> continuousQueries = new ArrayList<>();
//        QueryResult result = (QueryResult) metricDataRepo.query("SHOW CONTINUOUS QUERIES");
//        for (Result r : result.getResults()) {
//            for (Series s : r.getSeries()) {
//                if (s.getValues() != null) {
//                    for (List<Object> c : s.getValues()) {
//
//                        ContinuousQueryAdapter cq = new ContinuousQueryAdapter((String) c.get(0), (String) c.get(1));
//                        if (cq.name.contains(metricName))
//                            continuousQueries.add(cq);
//                    }
//                }
//
//            }
//        }
//        return ResponseEntity.ok(continuousQueries);
//    }
//
//    @GetMapping(value = "/granularities", produces = "application/json")
//    public ResponseEntity<?> listGranularities() {
//
//        return ResponseEntity.ok(metricDefaults.getGranularities());
//    }
//
//    @GetMapping(value = "/functions", produces = "application/json")
//    public ResponseEntity<?> listfunctions() {
//
//        return ResponseEntity.ok(metricDefaults.getFunctions());
//    }
//
//    @DeleteMapping(value = "/continuousQueries/delete", produces = "application/json")
//    public ResponseEntity<?> deleteContinuousQueriesByMetric(@RequestParam(value = "queryName") String queryName) throws Exception {
//
//
//        Object result = metricDataRepo.query("DROP CONTINUOUS QUERIES" + queryName + " ON " + database);
//        return ResponseEntity.ok(result);
//    }
//
//
//}
//
//
//
//
//
