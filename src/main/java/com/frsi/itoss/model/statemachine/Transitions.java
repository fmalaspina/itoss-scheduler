package com.frsi.itoss.model.statemachine;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Data
public final class Transitions {
    private List<AllowableTransitions> allowed = Arrays.asList(new AllowableTransitions(CtEvent.OPERATE, CtState.DELIVERY, CtState.OPERATIONS),
            new AllowableTransitions(CtEvent.DISABLE, CtState.OPERATIONS, CtState.OUTOFSERVICE),
            new AllowableTransitions(CtEvent.ENABLE, CtState.OUTOFSERVICE, CtState.OPERATIONS),
            new AllowableTransitions(CtEvent.ENABLE, CtState.MAINTENANCE, CtState.OPERATIONS),
            new AllowableTransitions(CtEvent.DISPOSE, CtState.DELIVERY, CtState.ENDOFSERVICE),
            new AllowableTransitions(CtEvent.DISPOSE, CtState.OPERATIONS, CtState.ENDOFSERVICE),
            new AllowableTransitions(CtEvent.DISPOSE, CtState.MAINTENANCE, CtState.ENDOFSERVICE),
            new AllowableTransitions(CtEvent.DISPOSE, CtState.OUTOFSERVICE, CtState.ENDOFSERVICE),
            new AllowableTransitions(CtEvent.OUTAGE_START, CtState.OPERATIONS, CtState.MAINTENANCE),
            new AllowableTransitions(CtEvent.OUTAGE_END, CtState.MAINTENANCE, CtState.OPERATIONS)

    );

    public boolean isAllowed(CtEvent event, CtState ctState) {

        return allowed.stream().anyMatch(a -> a.getEvent().equals(event) && a.getFromState().equals(ctState));
    }

    public CtState getToState(CtEvent event) {
        Optional<AllowableTransitions> transition = allowed.stream().filter(t -> t.getEvent().equals(event)).findFirst();
        return transition.get().getToState();
    }
}


@Data
@AllArgsConstructor
@NoArgsConstructor
class AllowableTransitions {
    private CtEvent event;
    private CtState fromState;
    private CtState toState;
}
