package com.frsi.itoss.shared;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StatusSnapshot implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private List<CtStatusPayload> statusSnapshot = new ArrayList<>();
}
