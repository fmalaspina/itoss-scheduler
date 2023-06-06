package com.frsi.itoss.shared;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

public record Task(Long taskId, Long ctId, Map<String, Object> payload,
                   Date previousFireTime) implements Serializable {
}
