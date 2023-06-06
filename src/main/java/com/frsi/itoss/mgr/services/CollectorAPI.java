package com.frsi.itoss.mgr.services;

import com.frsi.itoss.shared.CollectorConfiguration;
import com.frsi.itoss.shared.CollectorResponse;
import com.frsi.itoss.shared.TaskContext;
import com.frsi.itoss.shared.TaskResult;

import java.util.List;

public interface CollectorAPI {
    List<CollectorResponse> test(/*@JsonRpcParam(value = "cc") */CollectorConfiguration cc);

    //boolean saveConfiguration(/*@JsonRpcParam(value = "cc")*/ CollectorConfiguration cc);

    boolean resetCt(/*@JsonRpcParam(value = "id")*/ Long id);

    CollectorResponse runTool(/*@JsonRpcParam(value = "cc")*/ CollectorConfiguration cc);

    TaskResult runTask(/*@JsonRpcParam(value = "tc")*/ TaskContext tc);

}