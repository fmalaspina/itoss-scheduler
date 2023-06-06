package com.frsi.itoss.model.statemachine;

import com.frsi.itoss.model.ct.Ct;
import com.frsi.itoss.model.repository.CtRepo;
import com.frsi.itoss.model.repository.CtStatusRepo;
import com.frsi.itoss.shared.CtStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public final class StateMachine {

    @Autowired
    CtRepo ctRepo;
    @Autowired
    CtStatusRepo ctStatusRepo;
    Transitions transitions = new Transitions();

    public boolean sendEvent(CtEvent event, Ct ct) {
        Ct ctFound = ctRepo.findById(ct.getId()).get();
        if (transitions.isAllowed(event, ctFound.getState())) {

            ctFound.setState(transitions.getToState(event));

            if (ctFound.getState() != CtState.OPERATIONS) {
                ctFound.setStatus(null);
                ctFound = ctRepo.save(ctFound);

                Optional<CtStatus> ctStatusFound = ctStatusRepo.findById(ctFound.getId());
                if (ctStatusFound.isPresent()) {
                    ctStatusRepo.delete(ctStatusFound.get());
                }
            } else {
                ctRepo.save(ctFound);
            }
            return true;
        } else {
            return false;
        }
    }

}
