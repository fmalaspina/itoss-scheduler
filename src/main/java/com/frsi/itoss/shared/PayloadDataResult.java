package com.frsi.itoss.shared;

import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class PayloadDataResult implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private String rawResult;
    private ProcessingPhase phase = ProcessingPhase.Detail;
    private Map<String, Object> tags = new HashMap<String, Object>();
    private Map<String, Object> fields = new HashMap<String, Object>();
    private String error;
    private List<RuleEvaluationResult> evaluationResult = new ArrayList<>();
    private List<String> actionLog = new ArrayList<>();
}