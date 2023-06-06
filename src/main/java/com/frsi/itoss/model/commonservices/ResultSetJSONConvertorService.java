package com.frsi.itoss.model.commonservices;

import com.frsi.itoss.shared.Utils;
import com.zaxxer.hikari.HikariDataSource;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

@Service
public class ResultSetJSONConvertorService {


    //    @Value("${spring.datasource.url}")
//    String dataSourceEndpoint;
//    @Value("${spring.datasource.username}")
//    String dataSourceUsername;
//    @Value("${spring.datasource.password}")
//    String dataSourcePassword;
    private final HikariDataSource appDataSource;

    public ResultSetJSONConvertorService(@Qualifier("appDataSource") HikariDataSource appDataSource) {
        this.appDataSource = appDataSource;
    }

    public ResponseEntity<?> execute(String query) throws SQLException, Exception {

        try (Connection con = appDataSource.getConnection()) {

            PreparedStatement stmt = con.prepareStatement(query);

            ResultSet rs = stmt.executeQuery();

            JSONArray ja = Utils.convertResultSetIntoJSON(rs);

            rs.close();
            stmt.close();
            return ResponseEntity.ok(ja.toString());
        }

    }

    public JSONArray executeQuery(String query) throws SQLException, Exception {

        try (Connection con = appDataSource.getConnection()) {

            PreparedStatement stmt = con.prepareStatement(query);

            ResultSet rs = stmt.executeQuery();
            JSONArray ja = Utils.convertResultSetIntoJSON(rs);

            rs.close();
            stmt.close();
            return ja;
        }

    }


    public ResponseEntity<?> executePageable(String queryData, String queryCount) throws SQLException, Exception {

        try (Connection con = appDataSource.getConnection()) {

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


    public int converBooleanIntoInt(boolean bool) {
        if (bool)
            return 1;
        else
            return 0;
    }

    public int convertBooleanStringIntoInt(String bool) {
        if (bool.equals("false"))
            return 0;
        else if (bool.equals("true"))
            return 1;
        else {
            throw new IllegalArgumentException("wrong value is passed to the method. Value is " + bool);
        }
    }

    public double getDoubleOutOfString(String value, String format, Locale locale) {
        DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols(locale);
        otherSymbols.setDecimalSeparator('.');
        DecimalFormat f = new DecimalFormat(format, otherSymbols);
        String formattedValue = f.format(Double.parseDouble(value));
        double number = Double.parseDouble(formattedValue);
        return Math.round(number * 100.0) / 100.0;
    }

}