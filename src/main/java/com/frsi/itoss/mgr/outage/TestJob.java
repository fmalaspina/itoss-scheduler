package com.frsi.itoss.mgr.outage;

import org.quartz.Job;
import org.quartz.JobExecutionContext;

import java.util.Date;

public class TestJob implements Job {

    @Override
    public void execute(JobExecutionContext jobExecutionContext) {
        System.out.println("Hello Scheduler World! Fired on " + new Date());
    }
}