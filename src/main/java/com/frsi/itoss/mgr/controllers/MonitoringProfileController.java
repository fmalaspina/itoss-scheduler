package com.frsi.itoss.mgr.controllers;

import com.frsi.itoss.model.commonservices.ResultSetJSONConvertorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLException;

@RestController
@RequestMapping("/monitoringProfile/search")
public class MonitoringProfileController {


    @Autowired
    ResultSetJSONConvertorService convertor;


    @RequestMapping(value = "/findByMonitoringProfileId", method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity<?> findByMonitoringProfileId(@RequestParam("id") Long id) throws SQLException, Exception {
        String query =
                "select m.id monitor_id," +
                        " m.name monitor," +
                        "m.frequency_expression monitor_frequency," +
                        "CASE WHEN me.metric_category = 'Status' THEN true " +
                        "         ELSE false END AS metric_status," +
                        "r.name rule_name," +
                        "r.description rule_description," +
                        "r.priority rule_priority," +
                        "r.condition rule_condition," +
                        "r.actions rule_actions " +
                        "from monitoring_profile_monitors mpm " +
                        "inner join monitor m on m.id = mpm.monitors_id " +
                        "inner join metric me on me.id = m.metric_id " +
                        "inner join event_rule r on r.monitor_id = m.id and (r.ct_id is null) " +
                        "where mpm.monitoring_profile_id = " + id + " " +
                        "order by me.metric_category desc, m.name asc, r.priority asc, r.name asc";
        return convertor.execute(query);

    }

    @RequestMapping(value = "/findByCtId", method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity<?> findByCtId(@RequestParam("id") Long id) throws SQLException, Exception {
        String query =
                "WITH customrules " +
                        "AS (select m.id monitor_id, " +
                        "	   	   m.name monitor, " +
                        "	   	   m.frequency_expression monitor_frequency, " +
                        "          CASE WHEN me.metric_category = 'Status' THEN true " +
                        "				ELSE false END AS metric_status," +
                        "	   	   r.name rule_name, " +
                        "	   	   r.priority rule_priority," +
                        "	   	   r.condition rule_condition, " +
                        "	       r.actions rule_actions, " +
                        "	       r.ct_id rule_ct_id " +
                        "      from ct " +
                        "	  	   inner join monitoring_profile_monitors mpm on mpm.monitoring_profile_id = ct.monitoring_profile_id " +
                        "	  	   inner join monitor m on m.id = mpm.monitors_id " +
                        "	  	   inner join metric me on me.id = m.metric_id " +
                        "	  	   inner join event_rule r on r.monitor_id = m.id and r.ct_id = ct.id " +
                        "	 where ct.id = " + id +
                        ")  " +
                        " " +
                        "select *  " +
                        "  from (select m.id monitor_id, " +
                        "	   	   	   m.name monitor,  " +
                        "	   	   	   m.frequency_expression monitor_frequency, " +
                        "          CASE WHEN me.metric_category = 'Status' THEN true " +
                        "				ELSE false END AS metric_status," +
                        "	   	       r.name rule_name,  " +
                        "	   	   	   r.priority rule_priority, " +
                        "		   	   r.condition rule_condition, " +
                        "		       r.actions rule_actions, " +
                        "		       r.ct_id rule_ct_id " +
                        "		  from ct " +
                        "		  	   inner join monitoring_profile_monitors mpm on mpm.monitoring_profile_id = ct.monitoring_profile_id " +
                        "		  	   inner join monitor m on m.id = mpm.monitors_id and m.id not in (select distinct monitor_id from customrules) " +
                        "		  	   inner join metric me on me.id = m.metric_id " +
                        "		  	   inner join event_rule r on r.monitor_id = m.id and r.ct_id is null  " +
                        "		 where ct.id = " + id +
                        "	 union all  " +
                        "		select *  " +
                        "		  from customrules) as r " +
                        " order by metric_status desc, monitor asc, rule_priority asc, rule_name asc";
        return convertor.execute(query);

    }


}
