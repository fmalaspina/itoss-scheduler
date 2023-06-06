package com.frsi.itoss.model.repository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.frsi.itoss.mgr.services.AuditService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;
import java.util.Map;


@SpringBootTest
class CtRepositoryRevisionsTest {

    @Autowired
    AuditService auditService;

    @Test
    void testRevisionsBetweenDates() {
        var res = auditService.getRevisionsBetweenDates("ct", 0, 0L, "", "2021-08-24 16:00:00", "", 20,1);
        System.out.println(res.toString());
    }

    @Test
    void testRevision() {
        var revId = 2160744;
        var res = auditService.getRevision("ct", 200000L, revId);
        System.out.println(res.toString());
    }

    @Test
    void testRevisionDifference() {
        var rev1 = 2139940;
        var rev2 = 2160744;
        var res = auditService.getRevisionsDifferences("ct", 200000L, rev1, rev2);
        System.out.println(res.toString());
    }
}


