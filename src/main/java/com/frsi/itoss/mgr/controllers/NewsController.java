package com.frsi.itoss.mgr.controllers;

import com.frsi.itoss.model.commonservices.ResultSetJSONConvertorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.sql.SQLException;

@RestController
@RestResource
@RequestMapping("/news")
public class NewsController {


//    @Value("${spring.datasource.url}")
//    String dataSourceEndpoint;
//    @Value("${itoss.defaultTennantId:0}")
//    Long defaultTennantId;
//    @Value("${spring.datasource.username}")
//    String dataSourceUsername;
//    @Value("${spring.datasource.password}")
//    String dataSourcePassword;
@Autowired
ResultSetJSONConvertorService convertor;
    @PersistenceContext
    private EntityManager em;

    @RequestMapping(value = "/search/findByUserId", method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity<?> findNewsByUserId(@RequestParam("userId") Long userId) throws SQLException, Exception {

        String query =
                "WITH tennant AS (SELECT cts_id  FROM tennant_cts tc INNER JOIN tennant_users tu ON tu.tennant_id = tc.tennant_id " +
                        " WHERE tu.users_id = " + userId + " )	select n.*, a.status, a.timestamp as status_timestamp from news as n	"
                        + " left join news_acknowledge as a on n.id = a.news_id and a.user_id =	" + userId + " where (ct_id is null or ct_id in "
                        + "(select cts_id from tennant))	and (a.status is null or a.status != 'deleted') ORDER BY n.timestamp DESC";


        return convertor.execute(query);


    }


    @RequestMapping(value = "/search/countByUserId", method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity<?> countByUserId(@RequestParam("userId") Long userId) throws SQLException, Exception {

        String query =
                "WITH tennant AS (SELECT cts_id  FROM tennant_cts tc INNER JOIN tennant_users tu ON tu.tennant_id = tc.tennant_id " +
                        " WHERE tu.users_id = " + userId + " )	select n.type, n.tags, "
                        + "	   count(n.id) as total "
                        + "  from news n "
                        + " where n.id not in (select news_id "
                        + "   					  from news_acknowledge na "
                        + "   					 where n.id = na.news_id and na.user_id = " + userId + ")"
                        + "   	   and (n.ct_id is null or n.ct_id in (select cts_id from tennant))"
                        + " group by n.type, n.tags";


        return convertor.execute(query);


    }


}
