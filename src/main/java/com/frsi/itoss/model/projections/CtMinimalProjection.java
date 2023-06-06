package com.frsi.itoss.model.projections;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class CtMinimalProjection {
    private Long id;
    private String name;
    private String environment;
}
