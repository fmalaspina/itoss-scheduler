package com.frsi.itoss.mgr.controllers;

import com.frsi.itoss.shared.MetricCategory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Arrays;

@Controller
@RequestMapping("metrics")
public class MetricController {
    @GetMapping(value = "/categories", produces = "application/json")
    public ResponseEntity<?> categories() {

        return ResponseEntity.ok(Arrays.asList(MetricCategory.values()));
    }
}
