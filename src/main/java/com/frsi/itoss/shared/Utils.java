package com.frsi.itoss.shared;

import com.google.gson.Gson;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.http.ResponseEntity;

import java.sql.*;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;

public class Utils {

    public static boolean equals(Object prevObj, Object newObj) {
        final Gson gson = new Gson();
        String prevConvertedValue = gson.toJson(prevObj);
        String newConvertedValue = gson.toJson(newObj);
        if (!prevConvertedValue.equals(newConvertedValue)) {
            return false;
        } else {
            return true;
        }

    }


    public static <T> Predicate<T> distinctByKey(
            Function<? super T, ?> keyExtractor) {

        Map<Object, Boolean> seen = new ConcurrentHashMap<>();
        return t -> seen.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
    }

    public static Date convertDateToUTC() {
        return null;
    }


    /**
     * Get TimeScaleDB column names and type
     *
     * @param conn
     * @param metricName
     * @return Map<String, DataType> with name and type of ts db column
     * @throws SQLException
     */
    public static Map<String, DataType> getDBColumNameTypeMap(Connection conn, String metricName) throws SQLException {
        DatabaseMetaData databaseMetaData = conn.getMetaData();
        Map<String, DataType> dbColTypes = new HashMap<>();
        ResultSet columns = databaseMetaData.getColumns(null, null, metricName.toLowerCase(), "%");
        int columnCount = 0;


        while (columns.next()) {
            columnCount++;
            String columnName = columns.getString("COLUMN_NAME").toLowerCase();
            DataType columnType = getDataType(columns.getString("DATA_TYPE"));
            dbColTypes.put(columnName, columnType);
//            String columnsize = columns.getString("COLUMN_SIZE");
//            String decimaldigits = columns.getString("DECIMAL_DIGITS");
//            String isNullable = columns.getString("IS_NULLABLE");
//            String is_autoIncrment = columns.getString("IS_AUTOINCREMENT");
        }
        return dbColTypes;
    }


    public static DataType getDataType(String strType) {
        if ("-5".equals(strType)) return DataType.NUMBER;
        if ("12".equals(strType)) return DataType.TEXT;
        if ("5".equals(strType)) return DataType.NUMBER;
        if ("-7".equals(strType)) return DataType.BOOLEAN;
        if ("8".equals(strType)) return DataType.FLOAT;
        if ("93".equals(strType)) return DataType.TIME;
        return null;
    }


    public static boolean isSameType(DataType metricDataType, DataType columnDataType) {
        if (columnDataType == DataType.TEXT &&
                (metricDataType == DataType.PASSWORD ||
                        metricDataType == DataType.EMAIL ||
                        metricDataType == DataType.HOSTNAME ||
                        metricDataType == DataType.TEXT)) return true;
        if (columnDataType == DataType.NUMBER &&
                (metricDataType == DataType.NUMBER)) return true;
        if (columnDataType == DataType.NUMBER &&
                (metricDataType == DataType.NUMBER)) return true;
        if (columnDataType == DataType.FLOAT &&
                (metricDataType == DataType.FLOAT)) return true;
        if (columnDataType == DataType.TIME &&
                (metricDataType == DataType.TIME)) return true;
        if (columnDataType == DataType.BOOLEAN &&
                (metricDataType == DataType.BOOLEAN)) return true;

        return false;
    }

    public static String getColumnName(Object o) {
        if (o instanceof Tag) {
            return ((Tag) o).getName().toLowerCase().strip();
        } else {

            return ((Field) o).getName().toLowerCase().strip();
        }
    }

    public static DataType getColumnType(Object o) {
        if (o instanceof Tag) {
            return ((Tag) o).getType();
        } else {

            return ((Field) o).getType();
        }
    }

    public static String getMappedType(DataType t) {
        if (t == DataType.PASSWORD ||
                t == DataType.TEXT ||
                t == DataType.EMAIL ||
                t == DataType.HOSTNAME) {
            return "VARCHAR(1024)";
        }
        if (t == DataType.BOOLEAN) {
            return "BOOLEAN";
        }
        if (t == DataType.NUMBER) {
            return "BIGINT";
        }
        if (t == DataType.FLOAT) {
            return "FLOAT";
        }
        if (t == DataType.TIME) {
            return "TIMESTAMPTZ";
        }
        return "VARCHAR(1024)";
    }

    public static Timestamp convertStringToTimestamp(String strDate) {
        DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
        Instant instant = Instant.from(formatter.parse(strDate));
        Timestamp timeStampDate = new Timestamp(Instant.EPOCH.until(instant, ChronoUnit.MILLIS));
        return timeStampDate;
    }

    /**
     * Translates a data type from an integer (java.sql.Types value) to a string
     * that represents the corresponding class.
     * <p>
     * REFER: https://www.cis.upenn.edu/~bcpierce/courses/629/jdkdocs/guide/jdbc/getstart/mapping.doc.html
     *
     * @param type The java.sql.Types value to convert to its corresponding class.
     * @return The class that corresponds to the given java.sql.Types
     * value, or Object.class if the type has no known mapping.
     */
    public static Class<?> toClass(int type) {
        Class<?> result = Object.class;

        switch (type) {
            case Types.CHAR:
            case Types.VARCHAR:
            case Types.LONGVARCHAR:
                result = String.class;
                break;

            case Types.NUMERIC:
            case Types.DECIMAL:
                result = java.math.BigDecimal.class;
                break;

            case Types.BIT:
                result = Boolean.class;
                break;

            case Types.TINYINT:
                result = Byte.class;
                break;

            case Types.SMALLINT:
                result = Short.class;
                break;

            case Types.INTEGER:
                result = Integer.class;
                break;

            case Types.BIGINT:
                result = Long.class;
                break;

            case Types.REAL:
            case Types.FLOAT:
                result = Float.class;
                break;

            case Types.DOUBLE:
                result = Double.class;
                break;

            case Types.BINARY:
            case Types.VARBINARY:
            case Types.LONGVARBINARY:
                result = Byte[].class;
                break;

            case Types.DATE:
                result = java.sql.Date.class;
                break;

            case Types.TIME:
                result = Time.class;
                break;

            case Types.TIMESTAMP:
                result = Timestamp.class;
                break;
        }

        return result;
    }

    /**
     * Get TimeScaleDB column names
     *
     * @param conn
     * @param metricName
     * @return String with column names separated by commas
     * @throws SQLException
     */
    public static String getDBColumNames(Connection conn, String metricName) throws SQLException {
        DatabaseMetaData databaseMetaData = conn.getMetaData();
        ResultSet columns = databaseMetaData.getColumns(null, null, metricName.toLowerCase(), "%");
        int columnCount = 0;
        String columnNames = "";

        while (columns.next()) {
            columnCount++;
            columnNames = columnNames + columns.getString("COLUMN_NAME") + ",";
            //DataType datatype = getDataType(columns.getString("DATA_TYPE"));
//            String columnsize = columns.getString("COLUMN_SIZE");
//            String decimaldigits = columns.getString("DECIMAL_DIGITS");
//            String isNullable = columns.getString("IS_NULLABLE");
//            String is_autoIncrment = columns.getString("IS_AUTOINCREMENT");
        }
        return columnNames;
    }

    /**
     * Convert a result set into a JSON Array
     *
     * @param resultSet
     * @return a JSONArray
     * @throws Exception
     */
    public static JSONArray convertResultSetIntoJSON(ResultSet resultSet) throws Exception {
        JSONArray jsonArray = new JSONArray();
        while (resultSet.next()) {
            int total_rows = resultSet.getMetaData().getColumnCount();

            //ResultSetMetaData rsmd = resultSet.getMetaData();

            JSONObject obj = new JSONObject();
            for (int i = 0; i < total_rows; i++) {
                String columnName = resultSet.getMetaData().getColumnLabel(i + 1).toLowerCase();
                Object columnValue = resultSet.getObject(i + 1);

                // if value in DB is null, then we set it to default value
                if (columnValue == null) {
                    columnValue = "null";
                }
                /*
                 * Next if block is a hack. In case when in db we have values like price and
                 * price1 there's a bug in jdbc - both this names are getting stored as price in
                 * ResulSet. Therefore when we store second column value, we overwrite original
                 * value of price. To avoid that, i simply add 1 to be consistent with DB.
                 */

                if (obj.has(columnName)) {
                    columnName += "1";
                }

                obj.put(columnName, columnValue);
            }
            jsonArray.put(obj);
        }
        return jsonArray;
    }

    public static ResponseEntity<?> execute(String query, String dataSourceEndpoint, String dataSourceUsername, String dataSourcePassword) throws SQLException, Exception {

        try (Connection con = DriverManager.getConnection(dataSourceEndpoint, dataSourceUsername, dataSourcePassword)) {

            PreparedStatement stmt = con.prepareStatement(query);

            ResultSet rs = stmt.executeQuery();

            JSONArray ja = Utils.convertResultSetIntoJSON(rs);

            rs.close();
            stmt.close();
            return ResponseEntity.ok(ja.toString());
        }

    }

    public static ResponseEntity<?> executeNonQuery(String query, String dataSourceEndpoint, String dataSourceUsername, String dataSourcePassword) throws SQLException, Exception {

        try (Connection con = DriverManager.getConnection(dataSourceEndpoint, dataSourceUsername, dataSourcePassword)) {

            PreparedStatement stmt = con.prepareStatement(query);

            stmt.execute();

            stmt.close();
            return ResponseEntity.ok(null);
        }

    }


    public static JSONArray executeQueryWithPreNonQuery(String preQuery, String query, String dataSourceEndpoint, String dataSourceUsername, String dataSourcePassword) throws SQLException, Exception {
        JSONArray ja = null;
        try (Connection con = DriverManager.getConnection(dataSourceEndpoint, dataSourceUsername, dataSourcePassword)) {

            try (Statement stmt = con.createStatement()) {
                if (preQuery.length() > 0) {
                    query = preQuery + ";" + query;
                }

                stmt.execute(query);

                ResultSet rs = stmt.getResultSet();

                if (stmt.getMoreResults()) {

                    rs = stmt.getResultSet();

                }
                if (rs != null) {
                    ja = Utils.convertResultSetIntoJSON(rs);
                }

            }

        }
        return ja;


    }

    public static JSONArray executeQuery(String query, String dataSourceEndpoint, String dataSourceUsername, String dataSourcePassword) throws SQLException, Exception {

        try (Connection con = DriverManager.getConnection(dataSourceEndpoint, dataSourceUsername, dataSourcePassword)) {

            PreparedStatement stmt = con.prepareStatement(query);

            ResultSet rs = stmt.executeQuery();

            JSONArray ja = Utils.convertResultSetIntoJSON(rs);

            rs.close();
            stmt.close();
            return ja;
        }

    }

    public static ResponseEntity<?> executePageable(String queryData, String queryCount, String dataSourceEndpoint, String dataSourceUsername, String dataSourcePassword) throws SQLException, Exception {

        try (Connection con = DriverManager.getConnection(dataSourceEndpoint, dataSourceUsername, dataSourcePassword)) {

            PreparedStatement stmtCount = con.prepareStatement(queryCount);
            PreparedStatement stmtData = con.prepareStatement(queryData);

            ResultSet rsCount = stmtCount.executeQuery();
            ResultSet rsData = stmtData.executeQuery();


            JSONArray jaCount = Utils.convertResultSetIntoJSON(rsCount);
            JSONArray jaData = Utils.convertResultSetIntoJSON(rsData);
            JSONObject jaAll = new JSONObject();
            jaAll.put("page", jaCount.get(0));
            jaAll.put("data", jaData);
            rsCount.close();
            stmtCount.close();
            rsData.close();
            stmtData.close();
            return ResponseEntity.ok(jaAll.toString());


        }

    }
}
