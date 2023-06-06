package com.frsi.itoss.mgr.controllers;

import com.frsi.itoss.mgr.security.JwtUserDetailsService;
import com.frsi.itoss.mgr.services.CtEventService;
import com.frsi.itoss.mgr.services.CtService;
import com.frsi.itoss.model.ct.Ct;
import com.frsi.itoss.model.ct.CtHistory;
import com.frsi.itoss.model.repository.CtRepo;
import com.frsi.itoss.model.repository.CtStatusRepo;
import com.frsi.itoss.model.repository.DashboardEntryRepo;
import com.frsi.itoss.model.repository.InstrumentationRepo;
import com.frsi.itoss.model.statemachine.CtEvent;
import com.frsi.itoss.model.statemachine.CtEventPayload;
import com.frsi.itoss.model.user.UserAccount;
import com.frsi.itoss.shared.ApiError;
import com.frsi.itoss.shared.DashboardEntry;
import com.frsi.itoss.shared.DashboardEntryKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.concurrent.TimeoutException;

@Service
@RestController

public class EventController {

    @Autowired
    InstrumentationRepo instrumentationRepo;
    //    @Autowired
//    TicketCreationService ticketCreationService;
    @Autowired
    CtRepo ctRepo;
    @Autowired
    CtStatusRepo ctStatusRepo;
    @Autowired
    CtService ctService;
    @Autowired
    JwtUserDetailsService userDetailService;
    @Autowired
    private CtEventService ctEventService;
    @Autowired
    private DashboardEntryRepo dashboardEntryRepo;

    @RequestMapping(value = "/disable/{id}", method = RequestMethod.POST)
    public CtHistory sendDisableEvent(@PathVariable("id") Long id, @RequestBody CtEventPayload ctEventPayload) {

        ctEventPayload.setEvent(CtEvent.DISABLE);
        UserAccount user = userDetailService
                .getUserAccount(SecurityContextHolder.getContext().getAuthentication().getName());

        return ctEventService.updateState(id, ctEventPayload, user.getId());

    }

    @RequestMapping(value = "/enable/{id}", method = RequestMethod.POST)
    public CtHistory sendEnableEvent(@PathVariable("id") Long id, @RequestBody CtEventPayload ctEventPayload) {

        ctEventPayload.setEvent(CtEvent.ENABLE);
        UserAccount user = userDetailService
                .getUserAccount(SecurityContextHolder.getContext().getAuthentication().getName());

        return ctEventService.updateState(id, ctEventPayload, user.getId());


    }

    @RequestMapping(value = "/operate/{id}", method = RequestMethod.POST)
    public CtHistory sendOperateEvent(@PathVariable("id") Long id, @RequestBody CtEventPayload ctEventPayload) {

        ctEventPayload.setEvent(CtEvent.OPERATE);
        UserAccount user = userDetailService
                .getUserAccount(SecurityContextHolder.getContext().getAuthentication().getName());

        CtHistory result = ctEventService.updateState(id, ctEventPayload, user.getId());

        return result;

    }

    @RequestMapping(value = "/dispose/{id}", method = RequestMethod.POST)
    public CtHistory sendDisposeEvent(@PathVariable("id") Long id, @RequestBody CtEventPayload ctEventPayload) {

        ctEventPayload.setEvent(CtEvent.DISPOSE);
        UserAccount user = userDetailService
                .getUserAccount(SecurityContextHolder.getContext().getAuthentication().getName());

        CtHistory result = ctEventService.updateState(id, ctEventPayload, user.getId());

        return result;

    }

    @RequestMapping(value = "/history/{id}", method = RequestMethod.POST)
    public CtHistory sendHistEvent(@PathVariable("id") Long id, @RequestBody CtEventPayload ctEventPayload) {
        ctEventPayload.setEvent(CtEvent.HISTORY);
        UserAccount user = userDetailService
                .getUserAccount(SecurityContextHolder.getContext().getAuthentication().getName());

        CtHistory result = ctEventService.addCtHistory(id, ctEventPayload, user.getId());

        return result;

    }

    @RequestMapping(value = "/attend", method = RequestMethod.POST)
    public ResponseEntity<?> sendAttendEvent(@RequestParam("ctId") Long ctId, @RequestParam("monitorId") Long monitorId,
                                             @RequestParam("object") String object, @RequestBody CtEventPayload ctEventPayload) throws Exception {
        ctEventPayload.setEvent(CtEvent.ATTEND);
        DashboardEntryKey key = new DashboardEntryKey(ctId, monitorId, object);
        Optional<DashboardEntry> optionalDe = dashboardEntryRepo.findById(key);

        if (optionalDe.isPresent()) {
            DashboardEntry de = optionalDe.get();
            if (de.getAttended() != null && de.getAttended()) {
                ApiError error = new ApiError(HttpStatus.BAD_REQUEST);
                error.setErrors("The dashboard entry is already attended.");
                error.setMessage("The dashboard entry is already attended.");
                return ResponseEntity.badRequest().body(error);
            }
            de.setAttended(true);
            dashboardEntryRepo.saveAndFlush(de);

            UserAccount user = userDetailService
                    .getUserAccount(SecurityContextHolder.getContext().getAuthentication().getName());

            CtHistory result = ctEventService.addCtHistory(ctId, ctEventPayload, user.getId());
            return ResponseEntity.ok(result);
        }
        ApiError error = new ApiError(HttpStatus.BAD_REQUEST);
        error.setErrors("The dashboard entry does not exists any more.");
        error.setMessage("The dashboard entry does not exists.");
        return ResponseEntity.badRequest().body(error);

    }


    @RequestMapping(value = "/createTicket", method = RequestMethod.POST)
    public ResponseEntity<Object> createTicketEvent(@RequestParam("ctId") Long ctId, @RequestParam("monitorId") Long monitorId,
                                                    @RequestParam("object") String object, @RequestBody CtEventPayload ctEventPayload) throws Exception {
        return ResponseEntity.ok().body(ctEventService.createTicket(ctId, monitorId, object, ctEventPayload));
    }




    @GetMapping(value = {"reset/{id}"}, produces = "application/json")
    public CtHistory reset(@PathVariable(required = true) Long id) throws TimeoutException {
        CtHistory result = null;
        UserAccount user = userDetailService
                .getUserAccount(SecurityContextHolder.getContext().getAuthentication().getName());

        CtEventPayload ctEventPayload = new CtEventPayload();
        ctEventPayload.setEvent(CtEvent.RESET);
        ctEventPayload.setNotes("Reset action performed by operator.");
        Optional<Ct> optionalCt = ctRepo.findById(id);
        if (optionalCt.isPresent()) {
            result = ctEventService.addCtHistory(id, ctEventPayload, user.getId());
        }
        ctService.reset(id);

        return result;

    }

}