package com.frsi.itoss.shared;

import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor

@Entity
@TypeDefs({@TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)})
@Table(indexes = {
        @Index(name = "IDX_DASHBOARD_ENTRY_CONTAINER", columnList = "containerId"),
        @Index(name = "IDX_DASHBOARD_ENTRY_CT_CONTAINER", columnList = "ctId,containerId"),
        @Index(name = "IDX_DASHBOARD_ENTRY_COMPANY", columnList = "companyId")
})
public class DashboardEntry implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    @EmbeddedId
    private DashboardEntryKey id;
    private boolean fault;
    private Date createdAt;


    private Date modifiedAt;
    private String severity;
    private Date lastChange;
    // TODO generar un registro histórico por acción de attended
    private Boolean attended;
    @Column(precision = 10, scale = 2)
    private float score;
    private Long companyId;
    @Type(type = "jsonb")
    @Column(columnDefinition = "jsonb")
    @Basic(fetch = FetchType.EAGER)
    private MetricPayloadData metricPayloadData;

    private String ruleDescription;
    private Long ruleId;


    private Long containerId;


}

	
