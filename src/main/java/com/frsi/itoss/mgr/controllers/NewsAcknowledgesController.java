package com.frsi.itoss.mgr.controllers;

import com.frsi.itoss.model.commonservices.ResultSetJSONConvertorService;
import com.frsi.itoss.model.news.News;
import com.frsi.itoss.model.news.NewsAcknowledge;
import com.frsi.itoss.model.repository.NewsAcknowledgeRepo;
import com.frsi.itoss.model.repository.NewsRepo;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.Date;

@RestController
@RestResource
@RequestMapping("/newsAcknowledges")
public class NewsAcknowledgesController {

//    @Value("${spring.datasource.url}")
//    String dataSourceEndpoint;
//    @Value("${itoss.defaultTennantId:0}")
//    Long defaultTennantId;
//    @Value("${spring.datasource.username}")
//    String dataSourceUsername;
//    @Value("${spring.datasource.password}")
//    String dataSourcePassword;
@Autowired
NewsRepo newsRepo;
    @Autowired
    NewsAcknowledgeRepo newsAcknowledgeRepo;
    @Autowired
    ResultSetJSONConvertorService convertor;


    @Transactional
    @RequestMapping(value = "/deleteByUserId", method = RequestMethod.DELETE, produces = "application/json")
    public ResponseEntity<?> deleteNewsAcknowledgeByUserId(@RequestParam("userId") Long userId)
            throws SQLException, Exception {
        int deletedRecords = 0;
        for (News n : newsRepo.findByUserId(userId)) {

            NewsAcknowledge na = new NewsAcknowledge();
            na.setNews(n);
            na.setStatus("deleted");
            na.setTimestamp(new Date());
            na.setUserId(userId);
            newsAcknowledgeRepo.save(na);
            deletedRecords++;

        }
        return ResponseEntity.ok().body(new Response("deleted records:" + deletedRecords));

    }

    @RequestMapping(value = "/search/countByUserId", method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity<?> countByUserId(@RequestParam("userId") Long userId) throws SQLException, Exception {

        String query = "WITH tennant AS (SELECT cts_id  FROM tennant_cts tc INNER JOIN tennant_users tu ON tu.tennant_id = tc.tennant_id "
                + " WHERE tu.users_id = " + userId + " )	select n.type, n.tags, " + "	   count(n.id) as total "
                + "  from news n " + " where n.id not in (select news_id "
                + "   					  from news_acknowledge na "
                + "   					 where n.id = na.news_id and na.user_id = " + userId + ")"
                + "   	   and (n.ct_id is null or n.ct_id in (select cts_id from tennant))"
                + " group by n.type, n.tags";

        return convertor.execute(query);

    }

}

@Data
@AllArgsConstructor
class Response implements Serializable {
    private String message;
}
