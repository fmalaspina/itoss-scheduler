package com.frsi.itoss.mgr.controllers;

import com.frsi.itoss.mgr.health.ManagerSelfMonitorHealthService;
import lombok.extern.java.Log;
import org.jasypt.encryption.StringEncryptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.ParseException;
import java.util.Map;

@RestController
@Log
public class ManagerContoller {

    @Autowired
    @Qualifier("jasyptStringEncryptor")
    StringEncryptor encryptor;
    @Autowired
    ManagerSelfMonitorHealthService statisticService;


    @RequestMapping("/stats")

    public ResponseEntity<Map<String, Object>> stats() throws ParseException {
       // statisticService.stopWatch();
        return ResponseEntity.ok().body(statisticService.getAllInfo());
    }

//    @RequestMapping("/stats/reset")
//
//    public ResponseEntity<?> reset() {
//        statisticService.resetWatch();
//        return ResponseEntity.ok("Successfully reset.");
//    }

    @PostMapping(value = {"/encrypt"}, produces = "application/json")

    public ResponseEntity<?> encrypt(@RequestBody String string) {


        return ResponseEntity.ok(encryptor.encrypt(string));
    }
}
