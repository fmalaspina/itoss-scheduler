package com.frsi.itoss.mgr.controllers;

import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.ZoneId;

@RestController
@RestResource

public class ZoneIdsController {
    @GetMapping(value = {"/zoneIds"}, produces = "application/json")
    public ResponseEntity<?> get() {
        return ResponseEntity.ok(ZoneId.getAvailableZoneIds());

    }

}