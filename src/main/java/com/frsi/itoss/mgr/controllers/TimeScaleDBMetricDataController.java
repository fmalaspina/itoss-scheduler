package com.frsi.itoss.mgr.controllers;

import com.frsi.itoss.model.continuousqueries.MetricDefaults;
import com.frsi.itoss.model.inconsistency.Inconsistency;
import com.frsi.itoss.model.profile.Metric;
import com.frsi.itoss.model.repository.MetricRepo;
import com.frsi.itoss.model.repository.TimeScaleDBMetricDataRepoImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("metricData")
//@ConditionalOnProperty(
//        value = "itoss.timeseries.database",
//        havingValue = "timescale",
//        matchIfMissing = false)

public class TimeScaleDBMetricDataController {
    @Autowired(required = false)
    TimeScaleDBMetricDataRepoImpl metricDataRepo;
    @Autowired
    MetricDefaults metricDefaults;
    @Autowired
    MetricRepo metricRepo;

//    @Autowired
//    TimeseriesDAOServices timeseriesDAOServices;

    @PostMapping(value = "/query", produces = "application/json")
    public ResponseEntity<?> queryMetricDataWithPostParam(@RequestParam QueryString q) throws Exception {

        return ResponseEntity.ok(metricDataRepo.query(q.getQuery()));
    }

    @PostMapping(value = "/{id}/reset", produces = "application/json")
    public ResponseEntity<?> resetMetricDataWithParam(@PathVariable(required = true) Long id) {
        metricDataRepo.reset(id);
        return ResponseEntity.ok(null);
    }


    /**
     * @param metric
     * @return Lista de inconsistencias
     * @throws SQLException
     */
    @PostMapping(value = "/checkCreateTStable", produces = "application/json")
    public ResponseEntity<?> checkCreateTStable(@RequestBody Metric metric) throws SQLException {

        List<Inconsistency> inconsistencies = metricDataRepo.checkCreateTStable(metric);
        if (inconsistencies != null && !inconsistencies.isEmpty()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(inconsistencies);
        } else {

            return ResponseEntity.ok(inconsistencies);
        }
    }

    /**
     * Intenta arreglar las inconcistencias
     *
     * @param metric
     * @return Lista de inconsistencias y cuales pudo arreglar y cuales no
     * @throws SQLException
     */
    @PostMapping(value = "/fixInconsistencies", produces = "application/json")
    public ResponseEntity<?> fixInconsistencies(@RequestBody Metric metric) throws SQLException {

        List<Inconsistency> nonFixedInconsistencies = metricDataRepo.fixInconsistencies(metric).stream().filter(i -> !i.isFixed()).collect(Collectors.toList());
        if (nonFixedInconsistencies != null && !nonFixedInconsistencies.isEmpty()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(nonFixedInconsistencies);
        } else {
            return ResponseEntity.ok(null);
        }
    }

    /**
     * Borra y recrea tabla e hypertabla
     *
     * @param metric
     * @throws SQLException
     */
    @PostMapping(value = "/dropCreateTStable", produces = "application/json")
    public ResponseEntity<?> dropCreateTStable(@RequestBody Metric metric) throws SQLException {

        metricDataRepo.dropCreateTStable(metric);
        return ResponseEntity.ok(null);


    }


    @GetMapping(value = "/query", produces = "application/json")
    public ResponseEntity<?> queryMetricDataWithGetParam(@RequestParam QueryString q) throws Exception {

        return ResponseEntity.ok(metricDataRepo.query(q.getQuery()).toString());
    }

    @GetMapping(value = "/lastMetricByCt")
    public List<Map<String, Object>> lastMetricByCt(@RequestParam(value = "ctId") Long ctId,
                                                    @RequestParam(value = "metricName") String metricName, @RequestParam(value = "tz") String tz) throws Exception {


        String tags = getTags(metricName);

        StringBuilder sb = new StringBuilder();


        sb.append("SELECT " + metricDataRepo.getLastColumnsTimestampedAndRounded(metricName, tz) + " FROM ");
        sb.append(metricName.toLowerCase());
        sb.append(" WHERE ct_id=");
        sb.append(ctId);
        sb.append(" group by " + tags + " ORDER BY " + tags);

        return metricDataRepo.query(sb.toString());

    }

    private String getTags(String metricName) throws Exception {
        Optional<Metric> metric = metricRepo.findByName(metricName);
        String s = "ct_id";
        String tags = "";
        if (metric.isPresent()) {
            tags = metric.get().getTagsForSaveInPhase().map(t -> t.getName()).collect(Collectors.joining(","));

        }
        return tags.length() > 0 ? s + "," + tags : s;


    }


    private String getLastFields(String metricName, String tz) {
        Optional<Metric> metric = metricRepo.findByName(metricName);
        String lastFields = "last(time " + metricDataRepo.addAtTimeZone(tz) + ",time) as time";
        String addLastFields = "";
        if (metric.isPresent()) {
            addLastFields = metric.get().getFieldsForSaveInPhase().map(field -> {

                return "last(" + metricDataRepo.addAtTimeZoneIfTimestampField(metricName, field.getName(), tz) + ",time) AS " + "\"" + field.getName().toLowerCase() + "\"";
            }).collect(Collectors.joining(","));

        }
        return addLastFields.length() > 0 ? lastFields + "," + addLastFields : lastFields;


    }


    @GetMapping(value = "/metricByCtLastHour", produces = "application/json")
    public ResponseEntity<?> queryMetricDataByCtLastHour(@RequestParam(value = "ctId") Long ctId,
                                                         @RequestParam(value = "metricName") String metricName, @RequestParam(value = "tz") String tz) throws Exception {

        StringBuilder sb = new StringBuilder();

        sb.append("SELECT " + metricDataRepo.getAllColumnsTimestampedAndRounded(metricName, tz) + " FROM ");
        sb.append(metricName);
        sb.append(" WHERE ctId=");
        sb.append(ctId);
        sb.append(" AND time > now() - '1 hour'::interval  ORDER BY time DESC ");

        return ResponseEntity.ok(metricDataRepo.query(sb.toString()));
    }

    @GetMapping(value = "/metricByCtLastDay", produces = "application/json")
    public ResponseEntity<?> queryMetricDataByCtLastDay(@RequestParam(value = "ctId") Long ctId,
                                                        @RequestParam(value = "metricName") String metricName, @RequestParam(value = "tz") String tz) throws Exception {

        StringBuilder sb = new StringBuilder();

        sb.append("SELECT " + metricDataRepo.getAllColumnsTimestampedAndRounded(metricName, tz) + " FROM ");
        sb.append(metricName);
        sb.append(" WHERE ct_id=");
        sb.append(ctId);
        sb.append(" AND time > now() - '1 day'::interval  ORDER BY time  DESC");

        return ResponseEntity.ok(metricDataRepo.query(sb.toString()));
    }

    @GetMapping(value = "/metricByCtLastWeek", produces = "application/json")
    public ResponseEntity<?> queryMetricDataByCtLastWeek(@RequestParam(value = "ctId") Long ctId,
                                                         @RequestParam(value = "metricName") String metricName, @RequestParam(value = "tz") String tz) throws Exception {

        StringBuilder sb = new StringBuilder();

        sb.append("SELECT " + metricDataRepo.getAllColumnsTimestampedAndRounded(metricName, tz) + " FROM ");
        sb.append(metricName);
        sb.append(" WHERE ct_id=");
        sb.append(ctId);
        sb.append(" AND time > now() - '1 week'::interval  ORDER BY time DESC ");

        return ResponseEntity.ok(metricDataRepo.query(sb.toString()));
    }

    @GetMapping(value = "/metricByCtLastMonth", produces = "application/json")
    public ResponseEntity<?> queryMetricDataByCtLastMonth(@RequestParam(value = "ctId") Long ctId,
                                                          @RequestParam(value = "metricName") String metricName, @RequestParam(value = "tz") String tz) throws Exception {

        StringBuilder sb = new StringBuilder();

        sb.append("SELECT " + metricDataRepo.getAllColumnsTimestampedAndRounded(metricName, tz) + " FROM ");
        sb.append(metricName);
        sb.append(" WHERE ct_id=");
        sb.append(ctId);
        sb.append(" AND time > now() - '30 days'::interval  ORDER BY time DESC ");

        return ResponseEntity.ok(metricDataRepo.query(sb.toString()));
    }

    @GetMapping(value = "/metricByCtBetweenDates", produces = "application/json")
    public ResponseEntity<?> lastMetricByCt(@RequestParam(value = "ctId") Long ctId,
                                            @RequestParam(value = "metricName") String metricName, @RequestParam(value = "from") String from,
                                            @RequestParam(value = "to") String to, @RequestParam(value = "tz") String tz) throws Exception {

        StringBuilder sb = new StringBuilder();

        sb.append("SELECT " + metricDataRepo.getAllColumnsTimestampedAndRounded(metricName, tz) + " FROM ");
        sb.append(metricName);
        sb.append(" WHERE ct_id=");
        sb.append(ctId);
        sb.append(" AND time >= '");
        sb.append(from);
        sb.append("' AND time <= '");
        sb.append(to);
        sb.append("'  ORDER BY time ASC");

        return ResponseEntity.ok(metricDataRepo.query(sb.toString()));
    }

    @GetMapping(value = "/continuousQueries/list", produces = "application/json")
    public ResponseEntity<?> listContinuousQueriesByMetric(@RequestParam(value = "metricName") String metricName) throws Exception {
        String query = "SELECT * FROM timescaledb_information.continuous_aggregates where hypertable_name = '" + metricName + "';";
        return ResponseEntity.ok(metricDataRepo.query(query));

    }

    @GetMapping(value = "/granularities", produces = "application/json")
    public ResponseEntity<?> listGranularities() {

        return ResponseEntity.ok(metricDefaults.getGranularities());
    }

    @GetMapping(value = "/functions", produces = "application/json")
    public ResponseEntity<?> listfunctions() {

        return ResponseEntity.ok(metricDefaults.getFunctions());
    }

    @DeleteMapping(value = "/continuousQueries/delete", produces = "application/json")
    public ResponseEntity<?> deleteContinuousQueriesByMetric(@RequestParam(value = "queryName") String queryName) throws Exception {


        String query = "DROP MATERIALIZED VIEW " + queryName + " CASCADE;";
        return ResponseEntity.ok(metricDataRepo.nonQuery(query));
    }


}








