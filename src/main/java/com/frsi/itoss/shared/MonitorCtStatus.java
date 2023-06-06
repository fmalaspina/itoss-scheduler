package com.frsi.itoss.shared;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor

@Entity

@Table(indexes = {
        @Index(name = "IDX_MONITOR_CT_STATUS_MONITOR", columnList = "monitorId"),
        @Index(name = "IDX_MONITOR_CT_STATUS_CT", columnList = "ctId")

})
public class MonitorCtStatus implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    @EmbeddedId
    private MonitorCtKey id;

    @Column(length = 5000)
    private String error;
    private Date createdAt;
    private Date modifiedAt;
    private Date lastChange;
    @Column(length = 16)
    @Enumerated(EnumType.STRING)
    private MonitorStatus status;

    private Long collectorId;


}

	
