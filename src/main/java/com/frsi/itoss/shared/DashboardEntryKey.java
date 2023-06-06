package com.frsi.itoss.shared;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Embeddable

public class DashboardEntryKey implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private Long ctId;
    private Long monitorId;
    @Column(length = 5000)
    private String object = "";

}
