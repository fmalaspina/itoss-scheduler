package com.frsi.itoss.shared;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RuleEvaluationResult implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private String ruleName;
    private String ruleDescription;
    private boolean evaluationResult;

}
