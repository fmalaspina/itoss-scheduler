package com.frsi.itoss.shared;

import java.io.Serializable;

public record Mail(String[] destinations, String message, MetricPayloadData metricPayloadData,
                   Long toolId) implements Serializable {
};
