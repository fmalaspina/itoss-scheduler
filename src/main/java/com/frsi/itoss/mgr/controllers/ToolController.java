package com.frsi.itoss.mgr.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/runtool")
public class ToolController {

//    @Autowired
//    CtRepo ctRepo;
//    @Autowired
//    ToolRepo toolRepo;

//    @Value("${itoss.collector.api.timeout:10000}")
//    private int collectorApiTimeout;

    @Autowired
    ToolService toolService;

    @GetMapping(value = {"/{toolId}/{ctId}"}, produces = "application/json")
    public ResponseEntity<?> runTool(@PathVariable(required = true) Long toolId,
                                     @PathVariable(required = true) Long ctId) {
        return toolService.execute(toolId, ctId);
    }



}
