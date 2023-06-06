package com.frsi.itoss.mgr.outage;

import com.frsi.itoss.mgr.services.CtEventService;
import com.frsi.itoss.model.ct.Ct;
import com.frsi.itoss.model.repository.CtHistoryRepo;
import com.frsi.itoss.model.repository.CtRepo;
import com.frsi.itoss.model.statemachine.CtEvent;
import com.frsi.itoss.model.statemachine.CtEventPayload;
import lombok.extern.java.Log;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Component
@Log
public class OutageJob extends QuartzJobBean {

    @Autowired
    CtRepo ctRepo;
    @Autowired
    CtHistoryRepo ctHistoryRepo;
    @Autowired
    CtEventService ctChangeStatusService;

    @Override
    protected void executeInternal(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        log.info("Executing Job with key {}" + jobExecutionContext.getJobDetail().getDescription());
        JobDataMap jobDataMap = jobExecutionContext.getMergedJobDataMap();
        Long ctId = jobDataMap.getLongValue("ctId");
        Long userId = jobDataMap.getLongValue("userId");

        String type = jobDataMap.getString("type");
        String notes = jobDataMap.getString("notes");
        process(ctId, userId, type, notes);
    }


    /**
     * @param ctId
     * @param userId
     * @param type
     * @param notes
     */
    @Transactional
    void process(Long ctId, Long userId, String type, String notes) {
        Optional<Ct> ctFound = ctRepo.findById(ctId);

        if (ctFound.isPresent()) {
            //Ct ct = ctFound.get();


            CtEvent ctEvent;
            if (type.equals("start")) {
                ctEvent = CtEvent.OUTAGE_START;
            } else {
                ctEvent = CtEvent.OUTAGE_END;
            }

            final CtEventPayload changeStateDTO = new CtEventPayload(ctEvent, notes);
            ctChangeStatusService.updateState(ctId, changeStateDTO, userId);

        }
    }


}
