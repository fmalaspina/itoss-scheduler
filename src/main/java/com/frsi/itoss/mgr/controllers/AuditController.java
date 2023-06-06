package com.frsi.itoss.mgr.controllers;
import com.frsi.itoss.mgr.services.AuditService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/audit")
public class AuditController {
    private final AuditService auditService;

    @Autowired
    public AuditController(AuditService auditService) {
        this.auditService = auditService;
    }

    @GetMapping("/revisionsBetweenDates")
    public Map<String, Object> getRevisionsBetweenDates(
            @RequestParam String entity,
            @RequestParam(required = false,defaultValue = "-1") int revType,
            @RequestParam(required = false,defaultValue = "0") Long id,
            @RequestParam(required = false, defaultValue = "") String user,
            @RequestParam(required = false, defaultValue = "") String dateFrom,
            @RequestParam(required = false, defaultValue = "") String dateTo,
            @RequestParam(required = false,defaultValue = "20") int size,
            @RequestParam(required = false,defaultValue = "0") int page

    ) {
        return auditService.getRevisionsBetweenDates(entity, revType, id, user, dateFrom, dateTo, size,page);
    }

    @GetMapping("/revision")
    public List<Map<String, Object>> getRevision(
            @RequestParam String entity,
            @RequestParam Long id,
            @RequestParam Number revId
    ) {
        return auditService.getRevision(entity, id, revId);
    }

    @GetMapping("/revisionsDiff")
    public List<Map<String, Object>> getRevisionsDifferences(
            @RequestParam String entity,
            @RequestParam long id,
            @RequestParam int rev1,
            @RequestParam int rev2
    ) {
        return auditService.getRevisionsDifferences(entity, id, rev1, rev2);
    }
}

