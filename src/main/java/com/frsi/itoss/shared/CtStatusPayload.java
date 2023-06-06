package com.frsi.itoss.shared;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class CtStatusPayload implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    private boolean down;
    private Date lastStatusChange;

}
