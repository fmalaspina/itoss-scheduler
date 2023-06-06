package com.frsi.itoss.shared;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class IntegrationMessage {
    String ctType;
    String ctName;
    String ruleDescription;
    String metricName;
    String message;
}
