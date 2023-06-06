package com.frsi.itoss.mgr.controllers;

import com.frsi.itoss.model.commonservices.ResultSetJSONConvertorService;
import com.frsi.itoss.model.dynamicsearch.DynamicSearch;
import com.frsi.itoss.model.dynamicsearch.Source;
import com.frsi.itoss.model.repository.DynamicSearchRepo;
import com.frsi.itoss.model.repository.TimeScaleDBMetricDataRepoImpl;
import com.frsi.itoss.shared.Utils;
import com.zaxxer.hikari.HikariDataSource;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.java.Log;
import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Log
@RestController
@RequestMapping("dynamicSearch")
@Configuration
public class DynamicSearchController {

    private final HikariDataSource appDataSource;
    private final NamedParameterJdbcTemplate appJdbcTemplate;
    @Autowired
    TimeScaleDBMetricDataRepoImpl timescaleMetricDataRepo;


    @Autowired
    DynamicSearchRepo dynamicSearchRepo;
    //    @Autowired(required = false)
//    InfluxMetricDataRepoImpl influxMetricDataRepo;
//    @Value("${spring.datasource.url}")
//    String dataSourceEndpoint;
//    @Value("${spring.datasource.username}")
//    String dataSourceUsername;
//    @Value("${spring.datasource.password}")
//    String dataSourcePassword;
    @Autowired
    ResultSetJSONConvertorService convertor;

    public DynamicSearchController(@Qualifier("appDataSource") HikariDataSource appDataSource, @Qualifier("appJdbcTemplate") NamedParameterJdbcTemplate appJdbcTemplate) {
        this.appDataSource = appDataSource;
        this.appJdbcTemplate = appJdbcTemplate;
    }

    @PersistenceContext
    private EntityManager em;

    @PostMapping(value = "/{id}", produces = "application/json")
    @ResponseBody
    public ResponseEntity<?> execSearch(@PathVariable Long id, @RequestBody Params params) throws Exception {

        Optional<DynamicSearch> ds = dynamicSearchRepo.findById(id);
        if (ds.isPresent()) {
            return execute(params, ds.get());
        }
        return null;

    }

    @PostMapping(value = "/endpoints/{endpoint}", produces = "application/json")
    @ResponseBody
    public ResponseEntity<?> execSearch(@PathVariable String endpoint, @RequestBody Params params)
            throws SQLException, Exception {
        Optional<DynamicSearch> ds = dynamicSearchRepo.findByEndpoint(endpoint);
        if (ds.isPresent()) {
            return execute(params, ds.get());
        }
        return null;
    }

    /**
     * @param params
     * @param ds
     * @return
     * @throws SQLException
     * @throws Exception
     */
    private ResponseEntity<?> execute(Params params, DynamicSearch ds) throws SQLException, Exception {
        if (ds.getSource().equals(Source.POSTGRESQL)) {


            try (var con = appDataSource.getConnection()) {

                PreparedStatement stmt = con.prepareStatement(ds.getQuery());

                for (Param p : params.getParams()) {
                    stmt.setObject(p.getOrder(), p.getValue());
                }

                ResultSet rs = stmt.executeQuery();

                JSONArray ja = Utils.convertResultSetIntoJSON(rs);
                rs.close();
                stmt.close();
                return ResponseEntity.ok(ja.toString());
            }
        }
//        if (ds.getSource().equals(Source.INFLUXDB)) {
//            if (influxMetricDataRepo != null) {
//                return ResponseEntity.ok(influxMetricDataRepo.query(ds.getQuery()));
//            }
//        }
        if (ds.getSource().equals(Source.TIMESCALEDB)) {
            if (timescaleMetricDataRepo != null) {
                return ResponseEntity.ok(timescaleMetricDataRepo.query(ds.getQuery()));
            }
        }
        return null;
    }

}

@Data
@AllArgsConstructor
@NoArgsConstructor
class Params implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    List<Param> params = new ArrayList<Param>();
}

@Data
@AllArgsConstructor
@NoArgsConstructor
class Param implements Serializable {
    private static final long serialVersionUID = 1L;
    int order;
    Object value;
}
