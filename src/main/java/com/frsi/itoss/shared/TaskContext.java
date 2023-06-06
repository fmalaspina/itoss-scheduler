package com.frsi.itoss.shared;

import lombok.Data;

import java.util.*;

@Data
public class TaskContext {
    private Long targetId;
    private String instrumentation;
    private Long taskId;
    private String taskName;
    private Long timeout;
    private List<InstrumentationParameterValue> taskInstrumentationParameterValues;
    private List<InstrumentationParameterValue> targetInstrumentationParameterValues;
    private List<InstrumentationParameter> instrumentationParameters;
    private MetricPayloadData metricPayloadData;
    private Date previousFireTime = new Date();


    public boolean getTaskInstrumBooleanValue(String propertyName) {

        return (boolean) getTaskInstrumObject(propertyName);
    }


    public String getTaskInstrumStringValue(String propertyName) {
        return (String) getTaskInstrumObject(propertyName);
    }

    public <T> T getTaskInstrumValue(String propertyName, Class<T> type) {
        if (type == Boolean.class && getTaskInstrumObject(propertyName) == null) {
            return type.cast(true);
        }
        if (type == Map.class && (getTaskInstrumObject(propertyName) == "" || getTaskInstrumObject(propertyName) == null)) {
            return type.cast(new HashMap<String, String>());
        }
        return type.cast(getTaskInstrumObject(propertyName));
    }

    public int getMetricInstrumIntValue(String propertyName) {

        return (int) getTaskInstrumObject(propertyName);
    }

    public Object getTaskInstrumObject(String propertyName) {
        Object result;
        try {
            result = this.getTaskInstrumentationParameterValues().stream().filter(o -> o.getName().equals(propertyName)).findFirst().get()
                    .getValue();
        } catch (Exception e) {
            return null;
        }
        return result;
    }

    public Object getTargetInstrumObject(String propertyName) {
        Object instParam = this.targetInstrumentationParameterValues.stream().filter(o -> o.getName().equalsIgnoreCase(propertyName)).findFirst().get()
                .getValue();

        Optional<InstrumentationParameter> parameter = this.instrumentationParameters.stream().filter(i -> i.getName().equalsIgnoreCase(propertyName)).findFirst();

        if (parameter.isPresent() && parameter.get().getType().equals(DataType.PASSWORD)) {
            try {
                return CryptoService.decrypt((String) instParam);
            } catch (Exception e) {

                return "unable to decrypt.";
            }
        } else {
            return instParam;
        }
    }
}
