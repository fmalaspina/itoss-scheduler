package com.frsi.itoss.model.mail;

import com.frsi.itoss.shared.MetricPayloadData;
import lombok.AllArgsConstructor;
import lombok.Data;

@Deprecated
@Data
@AllArgsConstructor
public class Mail {
    private String[] destinations;
    private String message;
    private MetricPayloadData metricPayloadData;
    private Long toolId;
}
