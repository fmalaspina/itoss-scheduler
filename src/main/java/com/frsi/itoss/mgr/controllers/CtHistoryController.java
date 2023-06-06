package com.frsi.itoss.mgr.controllers;

import com.frsi.itoss.model.commonservices.ItossdbDAOServices;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/ctHistories")
public class CtHistoryController {


    private final ItossdbDAOServices itossdbDAOServices;

    public CtHistoryController(ItossdbDAOServices itossdbDAOServices) {
        this.itossdbDAOServices = itossdbDAOServices;
    }

    @RequestMapping(value = "/search/findByCtIdWithUserName", method = RequestMethod.GET, produces = "application/json")
    public List<Map<String, Object>> findfindByCtIdWithUserName(@RequestParam("ctId") Long ctId)
            throws Exception {


        String query = """
                        select ch.id,ch.created_by,ch.creation_date,ch.last_modified_by,ch.last_modified_date,
                        ch.attributes::text as attributes,ch.key,ch.event,ch.notes,ch.successful,ch.timestamp,ch.user_account_id,
                        ch.ct_id,ua.name  
                        from ct_history ch 
                        inner join user_account ua on ch.user_account_id = ua.id 
                        where ct_id = :ctId
                """;
        Map<String, Object> params = new HashMap<>();
        params.put("ctId", ctId);
        return itossdbDAOServices.queryForList(query, params);


    }

}
