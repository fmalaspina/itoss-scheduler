package com.frsi.itoss.mgr.services;

import com.frsi.itoss.mgr.security.JwtUserDetailsService;
import com.frsi.itoss.model.ct.Ct;
import com.frsi.itoss.model.ct.CtHistory;
import com.frsi.itoss.model.repository.CtRepo;
import com.frsi.itoss.model.repository.DashboardEntryRepo;
import com.frsi.itoss.model.repository.UserAccountRepo;
import com.frsi.itoss.model.statemachine.CtEventPayload;
import com.frsi.itoss.model.statemachine.StateMachine;
import com.frsi.itoss.model.user.UserAccount;
import com.frsi.itoss.shared.Attribute;
import com.frsi.itoss.shared.DashboardEntry;
import com.frsi.itoss.shared.DashboardEntryKey;
import com.frsi.itoss.shared.TaskResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.Optional;

@Service
public class CtEventService {
    @Autowired(required = false)
    TicketCreationService ticketCreationService;
    @Autowired
    DashboardEntryRepo dashboardEntryRepo;
    @Autowired
    JwtUserDetailsService userDetailService;

    @Autowired
    UserAccountRepo userAccountRepo;
    @Autowired
    StateMachine stateMachine;
    @Autowired
    private CtRepo ctRepo;

    @Transactional
    public CtHistory updateState(Long id, CtEventPayload ctEventPayload, Long userId) {
        try {
            LockByKey.lock(id);
            Ct ct = ctRepo.findById(id).get();
            boolean result = stateMachine.sendEvent(ctEventPayload.getEvent(), ct);
            ct = ctRepo.findById(id).get();
            CtHistory ctHistory = new CtHistory(new Date(), userId, ctEventPayload.getEvent(), ctEventPayload.getNotes(),
                    result);
            ct.getCtHistory().add(ctHistory);
            ctRepo.save(ct);
            return ctHistory;
        } finally {
            LockByKey.unlock(id);
        }

    }

    @Transactional
    public CtHistory addCtHistory(Long id, CtEventPayload ctEventPayload, Long userId) {
        Ct ct = ctRepo.findById(id).get();

        ct = ctRepo.findById(id).get();
        CtHistory ctHistory = new CtHistory(new Date(), userId, ctEventPayload.getEvent(), ctEventPayload.getNotes(),
                true);
        ctHistory.setAttributes(ctEventPayload.getAttributes());
        ct.getCtHistory().add(ctHistory);

        ctRepo.save(ct);
        return ctHistory;

    }

    public CtHistory createTicket(Long ctId, Long monitorId, String object, CtEventPayload ctEventPayload) throws Exception {
        UserAccount user = userDetailService.getUserAccount(SecurityContextHolder.getContext().getAuthentication().getName());

        TaskResult taskResult = ticketCreationService.createTicket(ctId, monitorId, object, ctEventPayload, user);

        DashboardEntryKey key = new DashboardEntryKey(ctId, monitorId, object);
        Optional<DashboardEntry> optionalDe = dashboardEntryRepo.findById(key);
        if (optionalDe.isPresent()) {
            DashboardEntry de = optionalDe.get();
            de.setAttended(true);
            dashboardEntryRepo.saveAndFlush(de);

            //String notes = ctEventPayload.getNotes();

            ctEventPayload.getAttributes().add(new Attribute("Ticket", taskResult));

            CtHistory result = this.addCtHistory(ctId, ctEventPayload, user.getId());
            return result;
        }
        return null;
    }

}