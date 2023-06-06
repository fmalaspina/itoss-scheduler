package com.frsi.itoss.mgr.controllers;

import org.mvel2.MVEL;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.Serializable;

@RestController
@RequestMapping("/mvel")
public class EventRuleValidatorController {

    @PostMapping(value = {"/validate"}, consumes = "application/json")
    public ResponseEntity<?> validate(@RequestBody String mvelExpression) {
        Serializable expr = MVEL.compileExpression(mvelExpression);
        return ResponseEntity.ok(expr.toString());
    }

}

