package com.frsi.itoss;

import com.frsi.itoss.model.repository.CollectorRepo;
import com.frsi.itoss.model.repository.CtRepo;
import com.frsi.itoss.model.statemachine.CtState;
import lombok.extern.java.Log;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@SpringBootTest(classes = ItossManagerApplication.class)
@Log

public class ManagerApiTest {
    @Autowired
    private CollectorRepo collRepo;
    @Autowired
    private CtRepo ctRepo;

    @PersistenceContext
    EntityManager entityManager;

    @Test
    void getConfigurationWithSpringProjections() {
        long startTime = System.currentTimeMillis();
        var result = ctRepo.findByCollectorIdAndState(200012L, CtState.OPERATIONS);
        long endTime = System.currentTimeMillis();

        log.info(String.valueOf(result.size()) + " elapsed time:" + String.valueOf(endTime - startTime));
    }


    @Test
    void findByUser() {
        long startTime = System.currentTimeMillis();
        var result = ctRepo.findByUser(200148L,
                "",
                0L,
                "",
                "",
                0L,
                0L,
                0L,
                "",
                "",
                PageRequest.of(0,20)
        );
        long endTime = System.currentTimeMillis();

        log.info(result.getTotalPages() + " elapsed time:" + String.valueOf(endTime - startTime));
    }


}
