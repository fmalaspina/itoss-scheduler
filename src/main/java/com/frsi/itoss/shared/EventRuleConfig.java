package com.frsi.itoss.shared;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventRuleConfig implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private Long id;
    private String description;
    private String name;
    private ProcessingPhase phase = ProcessingPhase.Detail; // Detail por defecto
    private int priority;
    private Long ctId;
    private String condition;
    private String actions;
    private boolean active;
    private Long counterThreshold = 0L;
    private Long counterResetTimeInterval = 0L;
    private String counterResetTimeIntervalUnit = "";
    /*
     * Consider first match within time windows as true, and false the others till finish threshold mathces or ttl window
     */
    private Boolean sendFirst = false;
    /*
     * Suppression active flag. Front end will show suppression fields only if true
     */

    private Boolean suppressActive = false;

    @JsonIgnore
    public Long getCounterResetTimeIntervalMillis() {

        switch (this.counterResetTimeIntervalUnit) {
            case "SECONDS":
                return this.counterResetTimeInterval * 1000;
            case "MINUTES":
                return this.counterResetTimeInterval * 60 * 1000;
            case "HOURS":
                return this.counterResetTimeInterval * 60 * 60 * 1000;

            default:
                throw new IllegalArgumentException("Unexpected value: " + this.counterResetTimeIntervalUnit);
        }
    }


}
