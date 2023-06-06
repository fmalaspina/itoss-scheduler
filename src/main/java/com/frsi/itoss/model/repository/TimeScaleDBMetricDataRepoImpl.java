package com.frsi.itoss.model.repository;

import com.frsi.itoss.model.baseclasses.CustomValidationException;
import com.frsi.itoss.model.commonservices.TimeseriesDAOServices;
import com.frsi.itoss.model.continuousqueries.ContinuousQuery;
import com.frsi.itoss.model.continuousqueries.MetricDefaults;
import com.frsi.itoss.model.inconsistency.Column;
import com.frsi.itoss.model.inconsistency.Inconsistency;
import com.frsi.itoss.model.profile.Metric;
import com.frsi.itoss.shared.*;
import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Repository

@Log
public class TimeScaleDBMetricDataRepoImpl implements MetricDataRepo {


    private final HikariDataSource timeseriesDataSource;
    private final NamedParameterJdbcTemplate timeseriesJdbcTemplate;
    @Autowired
    MetricDefaults metricDefaults;
    @Autowired
    MetricRepo metricRepo;
    @Autowired
    ContinuousQueryRepo continuousQueryRepo;

    @Autowired
    TimeseriesDAOServices timeseriesDAOServices;

    public TimeScaleDBMetricDataRepoImpl(@Qualifier("timeseriesDataSource") HikariDataSource timeseriesDataSource, @Qualifier("timeseriesJdbcTemplate") NamedParameterJdbcTemplate timeseriesJdbcTemplate) {
        this.timeseriesDataSource = timeseriesDataSource;
        this.timeseriesJdbcTemplate = timeseriesJdbcTemplate;
    }


    public Object nonQuery(String query) throws CustomValidationException {
        try {

            return timeseriesDAOServices.query(query);
        } catch (Exception e) {
            throw new CustomValidationException("Error executing query. " + e.getMessage());
        }
    }

    public void update(String query) throws CustomValidationException {
        try {

            timeseriesDAOServices.update(query);
        } catch (Exception e) {
            throw new CustomValidationException("Error executing query. " + e.getMessage());
        }
    }

    public List<Map<String, Object>> query(String query) throws CustomValidationException, SQLException {
        try {

            return timeseriesDAOServices.queryForList(query);
        } catch (BadSqlGrammarException e) {
            throw e;
        } catch (Exception e) {
            throw new CustomValidationException("Error executing query. " + e.getMessage());
        }
    }


    public void createContinuousQuery(ContinuousQuery continuousQuery) throws SQLException {


        String tags = "";
        tags = tags + continuousQuery.getMetric().getTagsForSaveInPhase().map(t -> "\"" + t.getName().toLowerCase() + "\"").collect(Collectors.joining(","));

        String metricName = continuousQuery.getMetric().getName().toLowerCase();
        String continuousQueryName = continuousQuery.getName().toLowerCase();
        String fieldsWithFunctions = continuousQuery.getFormatedFunctionsForTimescale();
        String granularityName = continuousQuery.getGranularity();
        String granularityExpanded = "";
        String offset = "";
        String refreshInterval = "";
        for (MetricDefaults.Granularity g : metricDefaults.getGranularities()) {
            if (g.getId().equals(granularityName)) {
                granularityExpanded = g.getName();
                offset = g.getOffset();
                refreshInterval = g.getRefreshInterval();
            }


        }


        if (tags.length() > 0) tags = "," + tags;

        String update = "DROP MATERIALIZED VIEW IF EXISTS " + continuousQueryName + "; CREATE MATERIALIZED VIEW " + continuousQueryName + " WITH (timescaledb.continuous) AS " +
                "SELECT time_bucket(INTERVAL '" + granularityExpanded + "', time) AS time, ct_id " + tags + ", " +
                fieldsWithFunctions + " FROM " + metricName +
                "  GROUP BY time_bucket(INTERVAL '" + granularityExpanded +
                "', time), ct_id" + tags + ";";


        String queryAddContinuousAggregate = "SELECT add_continuous_aggregate_policy('" + continuousQueryName +
                "',    start_offset => INTERVAL '" + offset + "', end_offset => INTERVAL '0 minutes',   " +
                "    schedule_interval => INTERVAL '" + refreshInterval + "')";

        String retention = "";
        switch (continuousQuery.getBucket()) {
            case "SMALL":
                retention = "24 weeks";
                break;
            case "EXTRA_SMALL":
                retention = "12 weeks";
                break;
            case "LARGE":
                retention = "104 weeks";
                break;
            case "EXTRA_LARGE":
                retention = "208 weeks";
                break;
            case "MEDIUM":
                retention = "52 weeks";
                break;

        }
        String queryAddRetentionPolicy = "SELECT add_retention_policy('" + continuousQueryName + "', INTERVAL '" + retention + "');";
        log.info("QUERY " + update + queryAddContinuousAggregate + queryAddRetentionPolicy);

        this.update(update);
        this.query(queryAddContinuousAggregate);
        this.query(queryAddRetentionPolicy);


    }


    public void deleteContinuousQuery(String queryName) {
        this.update("DROP MATERIALIZED VIEW IF EXISTS " + queryName);
    }


    public void createContinuousQuery(String query) {

    }


    public String getDatabaseName() {

        return null;
    }

    /**
     * Function that applies time zone if available
     */
    public String addAtTimeZone(String tz) {
        return tz != null && tz.length() > 0 ? " at time zone '" + tz + "'" : "";
    }

    /**
     * Applies time zone if field is timestamp and timezone is available
     */
    public String addAtTimeZoneIfTimestampField(String metricName, String column, String tz) {
        try {
            if (this.getTimestampColumns(metricName).contains(column)) {
                return "\"" + column.toLowerCase() + "\"" + addAtTimeZone(tz);

            }
        } catch (SQLException e) {
            return "\"" + column.toLowerCase() + "\"";
        }
        return "\"" + column.toLowerCase() + "\"";
    }

    /**
     * @param metricName
     * @return List of timestamp column names
     * @throws SQLException
     */
    public List<String> getTimestampColumns(String metricName) throws SQLException {
        Map<String, DataType> columnTypes;// = new HashMap<>();
        List<String> timeColumnNames;// = new ArrayList();
        try (var conn = timeseriesDataSource.getConnection()) {
            columnTypes = Utils.getDBColumNameTypeMap(conn, metricName);

            timeColumnNames = columnTypes.entrySet().stream().filter(e -> e.getValue() == DataType.TIME).map(e -> e.getKey()).collect(Collectors.toList());
        }
        return timeColumnNames;
    }

    public String getLastColumnsTimestampedAndRounded(String metricName, String tz) throws SQLException {
        Map<String, DataType> columnTypes;// = new HashMap<>();
        String timestampedColumns = "";
        //String lastFields = "last(time " + addAtTimeZone(tz) + ",time) as time";
        //String addLastFields = "";
        try (var conn = timeseriesDataSource.getConnection()) {
            columnTypes = Utils.getDBColumNameTypeMap(conn, metricName);

            timestampedColumns = columnTypes.entrySet().stream().map(e -> {

                String castStrSuffix = "";
                String castStrPrefix = "";
                if (e.getValue() == DataType.FLOAT) {
                    castStrSuffix = "::numeric,2)";
                    castStrPrefix = "round(";

                }


                if (e.getValue() == DataType.TIME) {
                    return "last(\"" + e.getKey() + "\"" + addAtTimeZone(tz) + ",time) as " + "\"" + e.getKey() + "\"";
                } else {
                    return "last(" + castStrPrefix + "\"" + e.getKey() + "\"" + castStrSuffix + ",time) as  \"" + e.getKey() + "\"";
                }
            }).collect(Collectors.joining(","));
        }
        return timestampedColumns;
    }


    public String getAllColumnsTimestampedAndRounded(String metricName, String tz) throws SQLException {
        Map<String, DataType> columnTypes;// = new HashMap<>();
        String timestampedColumns = "";
        try (var conn = timeseriesDataSource.getConnection()) {
            columnTypes = Utils.getDBColumNameTypeMap(conn, metricName);

            timestampedColumns = columnTypes.entrySet().stream().map(e -> {

                String castStrSuffix = "";
                String castStrPrefix = "";
                if (e.getValue() == DataType.FLOAT) {
                    castStrSuffix = "::numeric,2)";
                    castStrPrefix = "round(";

                }


                if (e.getValue() == DataType.TIME) {
                    return "\"" + e.getKey() + "\"" + addAtTimeZone(tz) + " as " + "\"" + e.getKey() + "\"";
                } else {
                    return castStrPrefix + "\"" + e.getKey() + "\"" + castStrSuffix + " as " + "\"" + e.getKey() + "\"";
                }
            }).collect(Collectors.joining(","));
        }
        return timestampedColumns;
    }

    public boolean isConsistentWithTS(Metric metric) throws SQLException {
        try (var conn = timeseriesDataSource.getConnection()) {
            if (checkInconsistencies(conn, metric).isEmpty()) {
                return true;
            } else {
                return false;
            }
        }
    }

    public List<Inconsistency> checkCreateTStable(Metric metric) throws SQLException {
        try (var conn = timeseriesDataSource.getConnection()) {

            try (Statement stmt = conn.createStatement()) {

                // check if metric exists
                String query = "SELECT to_regclass('" + metric.getName().toLowerCase() + "') as metric_name;";

                stmt.execute(query);

                // if not create table hypertable and index based on tags
                try (ResultSet rs = stmt.getResultSet()) {

                    rs.next();
                    String found = rs.getString("metric_name");
                    if (found == null || !found.equalsIgnoreCase(metric.getName())) {
                        createTable(metric, stmt);
                        Optional<Metric> metricFound = metricRepo.findById(metric.getId());
                        if (metricFound.isPresent()) {
                            metricFound.get().setIsConsistentWithTS(true);
                            metricRepo.save(metricFound.get());
                        }
                        createContinuousQueries(metric.getId());
                        return null;

                    } else {
                        // compare table
                        createContinuousQueries(metric.getId());
                        return checkInconsistencies(conn, metric);

                    }


                }
            }


        }

    }


    public void dropCreateTStable(Metric metric) throws SQLException {
        try (var conn = timeseriesDataSource.getConnection()) {
            try (Statement stmt = conn.createStatement()) {
                dropTable(metric, stmt);
                createTable(metric, stmt);
                Optional<Metric> metricFound = metricRepo.findById(metric.getId());
                if (metricFound.isPresent()) {
                    metricFound.get().setIsConsistentWithTS(true);
                    metricRepo.save(metricFound.get());
                }
            }
            createContinuousQueries(metric.getId());
        }
    }


    public void save(MetricPayloadData metric) throws SQLException {
//        try (Connection conn = DriverManager.getConnection(url, username, password)) {
//            Map<String, Object> metricColumns = new HashMap<String, Object>();
//            metricColumns.putAll(metric.getTags());
//            metricColumns.putAll(metric.getFields());
//            metricColumns.put("ct_id",metric.getCtId());
//            metricColumns.put("time", Utils.convertStringToTimestamp(metric.getTime()));
//
//            String columnaNames = Utils.getDBColumNames(conn,metric.getMetricName());
//
//            metricColumns.entrySet().removeIf(e -> !columnaNames.contains(e.getKey()));
//
//            String names = metricColumns.entrySet().stream().map(e -> e.getKey()).collect(Collectors.joining(","));
//            String values = metricColumns.entrySet().stream().map(e -> "?").collect(Collectors.joining(","));
//
//            String query = "INSERT INTO " + metric.getMetricName().toLowerCase() + "(" + names + ")" + " VALUES (" + values + ")";
//            System.out.println(query);
//            try (PreparedStatement stmt = conn.prepareStatement(query)) {
//                // stmt.execute(query);
//                int i = 1;
//                for (Map.Entry<String, Object> e : metricColumns.entrySet()) {
//
//                    stmt.setObject(i, e.getValue());
//                    i++;
//                }
//                for (i = 1; i <= stmt.getParameterMetaData().getParameterCount(); i++) {
//                    System.out.println(Utils.toClass(stmt.getParameterMetaData().getParameterType(i)));
//                }
//                stmt.execute();
//            }
//        }
    }


    public List<Inconsistency> fixInconsistencies(Metric metric) throws SQLException {

        try (var conn = timeseriesDataSource.getConnection()) {
            try (Statement stmt = conn.createStatement()) {
                // check if metric exists
                String query = "SELECT to_regclass('" + metric.getName().toLowerCase() + "') as metric_name;";

                stmt.execute(query);
                // if not create table hypertable and index based on tags
                try (ResultSet rs = stmt.getResultSet()) {

                    rs.next();
                    String found = rs.getString("metric_name");
                    if (found == null || !found.equalsIgnoreCase(
                            metric.getName())) {
                        throw new SQLException("Table not found.");

                    } else {
                        // compare table
                        List<Inconsistency> processedInconsistencies = new ArrayList<>();
                        List<Inconsistency> inconsistencies = checkInconsistencies(conn, metric);
                        if (!inconsistencies.isEmpty()) {
                            for (Inconsistency i : inconsistencies) {
                                String query2 = "ALTER TABLE " + metric.getName().toLowerCase();
                                if (i.isAddition()) {
                                    query2 = query2 + " ADD COLUMN ";
                                    query2 = query2 + " \"" + i.getMetricColumn().getName() + "\" " + Utils.getMappedType(i.getMetricColumn().getType());
                                    i.setTableColumn(new Column(i.getMetricColumn().getName(),
                                            i.getMetricColumn().getType()));
                                    //if (i.metricColumn.isPK) query2 = query2 + ", ADD PRIMARY KEY (time,ct_id," + i.metricColumn.name.toLowerCase() + ")";

                                } else {
                                    query2 = query2 + " ALTER COLUMN ";
                                    query2 = query2 + " \"" + i.getMetricColumn().getName() + "\" TYPE " + Utils.getMappedType(i.getMetricColumn().getType()) +
                                            " USING \"" + i.getMetricColumn().getName() + "\"::" + Utils.getMappedType(i.getMetricColumn().getType());

                                }
                                try {
                                    System.out.println(query2);
                                    stmt.execute(query2);
                                    i.setFixed(true);

                                } catch (Exception e) {
                                    i.setFixed(false);
                                    i.setError(e.getMessage().toString());
                                    i.setNeedsManualIntervention(true);
                                }
                                processedInconsistencies.add(i);

                            }
                        }

                        if (processedInconsistencies.isEmpty() || processedInconsistencies.stream().allMatch(pi -> pi.isFixed())) {
                            Optional<Metric> metricFound = metricRepo.findById(metric.getId());
                            if (metricFound.isPresent()) {
                                metricFound.get().setIsConsistentWithTS(true);

                                metricRepo.save(metricFound.get());
                            }
                        }


                        createContinuousQueries(metric.getId());


                        return processedInconsistencies;
                    }
                }
            }


        }

    }


    private void createContinuousQueries(Long metricId) {
        var continuousQueriesList = continuousQueryRepo.findByMetricId(metricId);
        continuousQueriesList.forEach(c -> {
            try {
                this.createContinuousQuery(c);
            } catch (Exception e) {
                log.severe(e.getMessage());
            }
        });
    }

    public void reset(Long metricId) throws CustomValidationException {
        Optional<Metric> metric = metricRepo.findById(metricId);
        try {
            if (metric.isPresent()) {
                nonQuery("truncate table " + metric.get().getName().toLowerCase());
            }
        } catch (Exception e) {
            throw new CustomValidationException("Could not reset metric data for metric " + metric.get().getName().toLowerCase() + " " + e.getMessage());
        }
    }

    private void dropTable(Metric metric, Statement stmt) throws SQLException {
        String query = "DROP TABLE IF EXISTS " + metric.getName().toLowerCase() + " CASCADE";

        stmt.execute(query);
    }

    private void createTable(Metric metric, Statement stmt) throws SQLException {
        List<Field> fields = metric.getFieldsAndVirtualFields();
        if (fields.size() == 0 && metric.getTagsForSaveInPhase().count() == 0)
            throw new SQLException("There are no fields to save in phase.");
        String query;

        List<String> tags = metric.getTagsForSaveInPhase().map(t -> t.getName().toLowerCase()).collect(Collectors.toList());

        tags.add("time");
        tags.add("ct_id");
//        String primaryKey = tags.stream().collect(Collectors.joining(","));
//        query = "CREATE TABLE IF NOT EXISTS " + metric.getName().toLowerCase() +
//
//                " (time TIMESTAMPTZ , ct_id BIGINT, PRIMARY KEY(" + primaryKey + ") ";


        query = "CREATE TABLE IF NOT EXISTS " + metric.getName().toLowerCase() +

                " (time TIMESTAMPTZ , ct_id BIGINT";


        for (Tag t : metric.getTagsForSaveInPhase().collect(Collectors.toList())) {

            query = query + ",\"" + t.getName().toLowerCase() + "\" " + Utils.getMappedType(t.getType());
        }


        for (Field f : fields) {

            query = query + ",\"" + f.getName().toLowerCase() + "\" " + Utils.getMappedType(f.getType());


        }

        query = query + ");";


        query = query + " select create_hypertable('" + metric.getName().toLowerCase() +
                "','time',chunk_time_interval => INTERVAL '1 week');";

        query = query + " SELECT add_retention_policy('" + metric.getName().toLowerCase() + "', INTERVAL '8 weeks');";
        log.info(query);

        stmt.execute(query);
    }


    public List<Inconsistency> checkInconsistencies(Connection conn, Metric metric) throws SQLException {
        DatabaseMetaData databaseMetaData = conn.getMetaData();


        var columns = databaseMetaData.getColumns(null, null, metric.getName().toLowerCase(), "%");
        // var primaryKeyColumns = databaseMetaData.getPrimaryKeys(null, null, metric.getName().toLowerCase());

        int columnCount = 0;
        //int pkColumnCount = 0;
        List<Field> fields = metric.getFieldsAndVirtualFields();

        List<Column> metricColumns = Stream.concat(
                fields.stream(), metric.getTagsForSaveInPhase()
        ).map((o) -> new Column(o)).collect(Collectors.toList());

//        List<Column> tagColumns =
//                metric.getTagsForSaveInPhase()
//                        .map((o) -> new Column(o.getName().toLowerCase())).collect(Collectors.toList());


        List<Column> dbColumns = new ArrayList<>();
        //List<Column> pkColumns = new ArrayList<>();
        List<Inconsistency> inconsistencies = new ArrayList<>();

        while (columns.next()) {
            columnCount++;
            String columnName = columns.getString("COLUMN_NAME");
            DataType datatype = Utils.getDataType(columns.getString("DATA_TYPE"));

            dbColumns.add(new Column(columnName, datatype));

        }



        metricColumns.forEach(metricColumn -> {
            Optional<Column> foundDBColumn = dbColumns.stream().filter((c) -> c.name.equals(metricColumn.name)).findFirst();
            if (foundDBColumn.isPresent()) {
                if (!Utils.isSameType(metricColumn.type, foundDBColumn.get().type)) {
                    inconsistencies.add(new Inconsistency(metricColumn, foundDBColumn.get(), false, "Metric field or tag " + metricColumn.getName() + " and timeseries database column " + foundDBColumn.get().getName() + " are of different types."));
                }
            } else {
                inconsistencies.add(new Inconsistency(metricColumn, null, true, "Metric field or tag " + metricColumn.getName() + " is not created within timeseries database."));

            }
        });


        if (columnCount == 0) {
            Inconsistency inconsistency = new Inconsistency();
            inconsistency.setError("Table " + metric.getName().toLowerCase() + " in timeseries does not exists.");

            inconsistencies.add(inconsistency);
        }
        if (inconsistencies.isEmpty()) {
            Optional<Metric> metricFound = metricRepo.findById(metric.getId());
            if (metricFound.isPresent()) {
                metricFound.get().setIsConsistentWithTS(true);

                metricRepo.save(metricFound.get());
            }
        }

        return inconsistencies;
    }


}
