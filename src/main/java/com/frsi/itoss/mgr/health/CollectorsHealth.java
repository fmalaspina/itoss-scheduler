package com.frsi.itoss.mgr.health;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class CollectorsHealth {
    private String name;
    private CollectorMetrics metrics;
}
