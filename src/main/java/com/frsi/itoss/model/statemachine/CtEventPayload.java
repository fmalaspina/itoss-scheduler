package com.frsi.itoss.model.statemachine;

import com.frsi.itoss.shared.Attribute;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CtEventPayload implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private CtEvent event;
    private String notes = new String("");
    private List<Attribute> attributes = new ArrayList<Attribute>();

    public CtEventPayload(CtEvent ctEvent, String notes) {
        this.event = ctEvent;
        this.notes = notes;
    }

}
