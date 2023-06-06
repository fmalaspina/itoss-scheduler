package com.frsi.itoss.mgr.services;

import com.frsi.itoss.shared.CtStatus;
import com.frsi.itoss.shared.ItossOperation;
import com.frsi.itoss.shared.ManagerAction;
import com.frsi.itoss.shared.Operation;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationEventPublisher;

import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
class CtStatusListenerTest {
    @Autowired
    ApplicationEventPublisher eventPublisher;

    @Test
    void test() throws InterruptedException {
        ManagerAction<CtStatus> event1 = new ManagerAction<CtStatus>();
        CtStatus ctStatusDown = new CtStatus();
        ctStatusDown.setId(201213L);
        ctStatusDown.setDown(true);
        event1.setActionObject(ctStatusDown);
        event1.setOperation(ItossOperation.SAVE_CT_STATUS);


        ManagerAction<CtStatus> event2 = new ManagerAction<CtStatus>();
        CtStatus ctStatusUp = new CtStatus();
        ctStatusUp.setId(201213L);
        ctStatusUp.setDown(false);
        event2.setActionObject(ctStatusUp);
        event2.setOperation(ItossOperation.SAVE_CT_STATUS);

        eventPublisher.publishEvent(event1);
        eventPublisher.publishEvent(event2);

        Scanner sc= new Scanner(System.in);
        System.out.println("Press Enter!!!");
        String str= sc.nextLine(); //reads string.


    }

}