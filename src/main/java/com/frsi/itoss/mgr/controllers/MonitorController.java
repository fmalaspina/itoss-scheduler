package com.frsi.itoss.mgr.controllers;

import com.frsi.itoss.model.profile.EventRule;
import com.frsi.itoss.model.profile.Monitor;
import com.frsi.itoss.model.repository.EventRuleRepo;
import com.frsi.itoss.model.repository.MonitorRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;
import java.util.Set;

@RestController
@RequestMapping("/monitors")
public class MonitorController {


    @Autowired
    MonitorRepo monitorRepo;
    @Autowired
    EventRuleRepo eventRuleRepo;


    @Transactional
    @PostMapping(value = {"/{monitorId}/copyRulesFor/{ctId}"}, produces = "application/json")
    public ResponseEntity<?> copyRules(@PathVariable(required = true) Long monitorId, @PathVariable(required = true) Long ctId) {


        Optional<Monitor> optionalMonitor = monitorRepo.findById(monitorId);
        if (optionalMonitor.isPresent()) {
            Monitor m = optionalMonitor.get();
            Set<EventRule> erList = m.getNonCustomRules();
            if (!erList.isEmpty()) {
                for (EventRule r : erList) {

                    EventRule newCustomRule = new EventRule();

                    newCustomRule.setCtId(ctId);
                    newCustomRule.setActions(r.getActions());
                    newCustomRule.setActive(r.isActive());
                    newCustomRule.setAttributes(r.getAttributes());
                    newCustomRule.setCondition(r.getCondition());
                    newCustomRule.setDescription(r.getDescription());
                    newCustomRule.setName(r.getName());
                    newCustomRule.setPriority(r.getPriority());
                    newCustomRule.setPhase(r.getPhase());
                    newCustomRule = eventRuleRepo.saveAndFlush(newCustomRule);
                    m.addEventRule(newCustomRule);
                    m = monitorRepo.saveAndFlush(m);

                }

                return ResponseEntity.ok(m.getCustomRules(ctId));
            }
        }
        return null;

    }
}

