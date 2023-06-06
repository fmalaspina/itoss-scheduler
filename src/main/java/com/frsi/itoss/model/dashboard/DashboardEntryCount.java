package com.frsi.itoss.model.dashboard;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DashboardEntryCount {


    private String severity;
    private String environment;
    private Long count;
}
