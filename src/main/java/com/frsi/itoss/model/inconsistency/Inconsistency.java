package com.frsi.itoss.model.inconsistency;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
public class Inconsistency implements Serializable {
    Column metricColumn;
    Column tableColumn;
    boolean isAddition;
    boolean isFixed;
    boolean needsManualIntervention;
    String error;

    public Inconsistency(Column metricColumn, Column tableColumn, boolean isAddition) {
        this.metricColumn = metricColumn;
        this.tableColumn = tableColumn;
        this.isAddition = isAddition;
    }

    public Inconsistency(Column metricColumn, Column tableColumn, boolean isAddition, String error) {
        this.metricColumn = metricColumn;
        this.tableColumn = tableColumn;
        this.isAddition = isAddition;
        this.error = error;
    }

    public void setFixed(boolean fixed) {
        isFixed = fixed;
    }

    public void setNeedsManualIntervention(boolean needsManualIntervention) {
        this.needsManualIntervention = needsManualIntervention;
    }

    public void setError(String error) {
        this.error = error;
    }

    @Override
    public String toString() {
        return "Inconsistency{" +
                "metricColumn=" + metricColumn +
                ", tableColumn=" + tableColumn +
                ", isAddition=" + isAddition +
                ", isFixed=" + isFixed +
                ", needsManualIntervention=" + needsManualIntervention +
                ", error='" + error + '\'' +
                '}';
    }

    public void setMetricColumn(Column metricColumn) {
        this.metricColumn = metricColumn;
    }

    public void setTableColumn(Column tableColumn) {
        this.tableColumn = tableColumn;
    }
}
