package com.frsi.itoss.mgr.health;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
@AllArgsConstructor
@Getter
@Setter
public class CollectorMetrics {
    private LocalDateTime last_config_request_timestamp;
    private Long last_config_request_seconds;
    private LocalDateTime last_message_received_timestamp;
    private Long last_message_received_seconds;
}
