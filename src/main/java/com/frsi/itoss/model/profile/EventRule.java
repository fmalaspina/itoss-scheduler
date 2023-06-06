package com.frsi.itoss.model.profile;

import com.frsi.itoss.model.baseclasses.EntityBase;
import com.frsi.itoss.shared.ProcessingPhase;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Data
@EqualsAndHashCode(callSuper=false)
@NoArgsConstructor
@AllArgsConstructor
@Audited
@Table(indexes = {
        @Index(name = "idx_event_rule_monitor_id", columnList = "monitor_id"),
        @Index(name = "idx_event_rule_ct_id", columnList = "ctId"),
        @Index(name = "idx_event_rule_phase", columnList = "phase")
})

public class EventRule extends EntityBase implements Serializable {

    /**
     *
     */

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "eventrule_gen")
    @SequenceGenerator(name = "eventrule_gen", sequenceName = "eventrule_seq", initialValue = 200000, allocationSize = 1)
    @Column(name = "id", updatable = false, nullable = false)
    private Long id;
    private String description;
    private String name;
    @Enumerated(EnumType.STRING)
    private ProcessingPhase phase = ProcessingPhase.Detail;
    private int priority;
    private Long ctId;
    @Column(columnDefinition = "TEXT")
    private String condition;
    @Column(columnDefinition = "TEXT")
    private String actions;
    private boolean active = true;
    /*
     * Suppression matches threshold
     */
    private Long counterThreshold = 0L;
    /*
     * TTL windows of suppression
     */

    private Long counterResetTimeInterval = 0L;
    private String counterResetTimeIntervalUnit = ""; // HOURS, MINUTES, SECONDS
    /*
     * Consider first match within time windows as true, and false the others till finish threshold mathces or ttl window
     */
    private Boolean sendFirst = false;
    /*
     * Suppression active flag. Front end will show suppression fields only if true
     */
    private Boolean suppressActive = false;


    public Long getCounterThreshold() {
        return (this.counterThreshold == null) ? 0L : this.counterThreshold;
    }

    public Long getCounterResetTimeInterval() {
        return (this.counterResetTimeInterval == null) ? 0L : this.counterResetTimeInterval;
    }

    public String getCounterResetTimeIntervalUnit() {
        return (this.counterResetTimeIntervalUnit == null || this.counterResetTimeIntervalUnit.isEmpty()) ? "SECONDS" : this.counterResetTimeIntervalUnit;
    }

    @PrePersist
    @PreUpdate
    @PostLoad
    private void setNullFields() throws Exception {
        if (sendFirst == null) this.setSendFirst(false);
        if (suppressActive == null) this.setSuppressActive(false);
        if (phase == null) this.setPhase(ProcessingPhase.Detail);
    }


}
