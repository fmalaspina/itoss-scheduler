package com.frsi.itoss.shared;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.java.Log;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Log
@Data
@AllArgsConstructor
@NoArgsConstructor
//@JsonFilter("MonitorConfigurationFilter")
public class MonitorConfiguration implements Serializable {


    private static final long serialVersionUID = 1L;
    List<EventRuleConfig> eventRules = new ArrayList<>();
    private Long monitorId;
    private String monitorName;
    private String frequencyExpression;
    private MetricPayloadAttributes metricPayloadAttributes;
    private String externalCommand;
    private String metricName;
    private long metricId;
    private String profileName;
    private String profileId;
    //private boolean statusMetric;
    private MetricCategory metricCategory;
    private InstrumentationAdapter instrumentationAdapter;
    private String scoringExpression;
    private List<InstrumentationParameterValue> monitorInstrumParamValues = new ArrayList<>();
    private List<InstrumentationParameterValue> metricInstrumParamValues = new ArrayList<>();
    private Set<CtConfiguration> ctConfiguration = new HashSet<>();
    private RuleEvaluationMode ruleEvaluationMode;
    private Long containerId = 0L;
    private boolean skipOnFirstAppliedRule = false;
    private boolean skipOnFirstFailedRule = false;
    private boolean skipOnFirstNonTriggeredRule = false;

    @JsonIgnore
    public EventRuleConfig getRuleConfig(String ruleName) {

        var ruleFound = this.eventRules.stream().filter(r -> r.getId().toString().equalsIgnoreCase(ruleName)).findFirst();
        if (ruleFound.isPresent()) {
            return ruleFound.get();
        } else {
            log.severe("Rule not found: " + ruleName);
            return null;
        }

    }

    @JsonIgnore
    public boolean isStatusMetric() {
        return this.getMetricCategory() == MetricCategory.Status;
    }

    @JsonIgnore
    public boolean isAvailabilityMetric() {
        return this.getMetricCategory() == MetricCategory.Availability;
    }


    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        MonitorConfiguration other = (MonitorConfiguration) obj;
        if (monitorId == null) {
            return other.monitorId == null;
        } else return monitorId.equals(other.monitorId);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((monitorId == null) ? 0 : monitorId.hashCode());
        return result;
    }

    public void add(CtConfiguration ctc) {
        this.ctConfiguration.add(ctc);
    }

    @JsonIgnore
    public CtConfiguration getCtConfigurationByCtId(String ctId) {
        var ctFound = this.ctConfiguration.stream().filter(ctc -> ctc.getCtId().equals(ctId)).findFirst();
        if (ctFound.isPresent()) {
            return ctFound.get();
        } else {
            log.severe("Ct configuration not found: " + ctId);
            return null;
        }
    }
    @JsonIgnore
    public int getMonitorInstrumIntValue(String propertyName) {

        return (int) getMonitorInstrumObject(propertyName);
    }

    @JsonIgnore
    public boolean getMonitorInstrumBooleanValue(String propertyName) {

        return (boolean) getMonitorInstrumObject(propertyName);
    }
    @JsonIgnore
    public boolean getMetricInstrumBooleanValue(String propertyName) {

        return (boolean) getMetricInstrumObject(propertyName);
    }
    @JsonIgnore
    public Object getMonitorInstrumObject(String propertyName) {

        // try first with a prefix and then without it
        var propertyNamePrefixed = getInstrumentationAdapter().getName() + "_" + propertyName;
        // try to find the property with the prefix
        try {
            var optionalProperty = this.monitorInstrumParamValues.stream().filter(o -> o.getName().equalsIgnoreCase(propertyNamePrefixed)).findFirst();
            if (optionalProperty.isPresent()) {
                return optionalProperty.get().getValue();
            } else {
                // try to find the property without the prefix
                optionalProperty = this.monitorInstrumParamValues.stream().filter(o -> o.getName().equalsIgnoreCase(propertyName)).findFirst();
                if (optionalProperty.isPresent()) {
                    return optionalProperty.get().getValue();
                } else {

                    return null;
                }
            }
        } catch (Exception ignored) {
            return null;
        }

    }
    @JsonIgnore
    public String getMetricInstrumStringValue(String propertyName) {
        return (String) getMetricInstrumObject(propertyName);
    }
    @JsonIgnore
    public <T> T getMetricInstrumValue(String propertyName, Class<T> type) {
        if (type == Boolean.class && getMetricInstrumObject(propertyName) == null) {
            return type.cast(true);
        }

        return type.cast(getMetricInstrumObject(propertyName));
    }
    @JsonIgnore
    public int getMetricInstrumIntValue(String propertyName) {

        return (int) getMetricInstrumObject(propertyName);
    }
    @JsonIgnore
    public Object getMetricInstrumObject(String propertyName) {
        // try first with a prefix and then without it
        var propertyNamePrefixed = getInstrumentationAdapter().getName() + "_" + propertyName;
        // try to find the property with the prefix
        try {
            var optionalProperty = this.metricInstrumParamValues.stream().filter(o -> o.getName().equalsIgnoreCase(propertyNamePrefixed)).findFirst();
            if (optionalProperty.isPresent()) {
                return optionalProperty.get().getValue();
            } else {
                // try to find the property without the prefix
                optionalProperty = this.metricInstrumParamValues.stream().filter(o -> o.getName().equalsIgnoreCase(propertyName)).findFirst();
                if (optionalProperty.isPresent()) {
                    return optionalProperty.get().getValue();
                } else {

                    return null;
                }
            }
        } catch (Exception ignored) {
            return null;
        }
    }


    @JsonIgnore
    public void add(InstrumentationParameterValue<?> object) {
        this.monitorInstrumParamValues.add(object);
    }

    @JsonIgnore
    public List<EventRuleConfig> getEventRules(Long ctId) {
        List<EventRuleConfig> particularRules = this.getEventRules().stream()
                .filter(r -> r.getCtId() != null && r.getCtId().equals(ctId)).collect(Collectors.toList());
        List<EventRuleConfig> generalRules = this.getEventRules().stream()
                .filter(r -> r.getCtId() == null || r.getCtId() == 0L).collect(Collectors.toList());
        if (!particularRules.isEmpty())
            return particularRules;
        return generalRules;
    }

    @JsonIgnore
    public <T> T getCtConfigurationByCtIdInstrumValue(String ctId, String parameterName, Class<T> type) {


        CtConfiguration ctConfigurationByCtId = this.getCtConfigurationByCtId(ctId);
        T parameterValue = null;
        // try first with a prefix and then without it
        var parameterNamePrefixed = getInstrumentationAdapter().getName() + "_" + parameterName;
        // try to find the property with the prefix
        try {
            var optionelParameterValue = ctConfigurationByCtId.getParamValues().entrySet().stream()
                    .filter(o -> o.getKey().equalsIgnoreCase(parameterNamePrefixed)).findFirst();

            if (optionelParameterValue.isPresent()) {
                parameterValue = type.cast(optionelParameterValue.get().getValue());

            } else {
                // try to find the property without the prefix
                optionelParameterValue = ctConfigurationByCtId.getParamValues().entrySet().stream()
                        .filter(o -> o.getKey().equalsIgnoreCase(parameterName)).findFirst();
                if (optionelParameterValue.isPresent()) {
                    parameterValue = type.cast(optionelParameterValue.get().getValue());
                }
            }

            // try to find the property without the prefix


            var optParamType = ctConfigurationByCtId.getTypeAttributes().stream()
                    .filter(o -> o.getName().equalsIgnoreCase(parameterNamePrefixed)).findFirst();
            DataType paramType;
            if (optParamType.isPresent()) {
                paramType = optParamType.get().getType();
            } else {
                // try without the prefix
                optParamType = ctConfigurationByCtId.getTypeAttributes().stream()
                        .filter(o -> o.getName().equalsIgnoreCase(parameterName)).findFirst();
                if (optParamType.isPresent()) {
                    paramType = optParamType.get().getType();
                } else {
                    paramType = this.instrumentationAdapter.getDataType(parameterName);

                }

            }


            // if password decrypt it
            if (paramType.equals(DataType.PASSWORD)) {
                try {
                    return type.cast(CryptoService.decrypt((String) parameterValue));
                } catch (Exception e) {

                    return type.cast("unable to decrypt.");
                }
            } else {
                return parameterValue;
            }
        } catch (Exception ignored) {
            return null;
        }
    }

    @JsonIgnore
    public Stream<MetricColumn> getPhaseFields(ProcessingPhase phase) {
        return getMetricPayloadAttributes().getFields().stream().filter(f -> f.getPhase() == phase).map(MetricColumn.class::cast);

    }
    @JsonIgnore
    public Stream<MetricColumn> getPhaseTags(ProcessingPhase phase) {
        return getMetricPayloadAttributes().getTags().stream().filter((MetricColumn t) -> t.getPhase() == phase).map(MetricColumn.class::cast);

    }

    @JsonIgnore
    public Stream<FieldOrTag> getPhaseFieldsAndTagsOrdered(ProcessingPhase phase) {
        List<FieldOrTag> combined = Stream.concat(
                getMetricPayloadAttributes().getFields().stream()
                        .filter(f -> f.getPhase() == phase),
                getMetricPayloadAttributes().getTags().stream()
                        .filter(t -> t.getPhase() == phase)).map(this::transform).toList();
        return combined.stream().sorted(Comparator.comparingInt(FieldOrTag::getOrdinal));

    }

    @JsonIgnore
    FieldOrTag transform(Object o) {
        if (o instanceof Field) {

            return new FieldOrTag("Field", ((Field) o).getName(), ((Field) o).getOrdinal());
        }
        if (o instanceof Tag) {
            return new FieldOrTag("Tag", ((Tag) o).getName(), ((Tag) o).getOrdinal());
        }
        return null;

    }


@Data
@AllArgsConstructor
public static
class FieldOrTag {
    private String clazz;
    private String name;
    private int ordinal;

}


}
