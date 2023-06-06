package com.frsi.itoss.mgr.controllers;

import com.frsi.itoss.mgr.outage.Outage;
import com.frsi.itoss.mgr.outage.OutageJob;
import com.frsi.itoss.mgr.outage.OutageScheduleResponse;
import com.frsi.itoss.mgr.security.JwtUserDetailsService;
import com.frsi.itoss.model.ct.Ct;
import com.frsi.itoss.model.repository.CtRepo;
import com.frsi.itoss.model.user.UserAccount;
import lombok.Data;
import lombok.extern.java.Log;
import org.quartz.*;
import org.quartz.impl.matchers.GroupMatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.*;

@Service
@RestController
@RequestMapping("outages")
@Log
public class OutageController implements Serializable {
    /**
     *
     */

    private static final long serialVersionUID = 1L;
    @Autowired
    public Scheduler scheduler;
    @Autowired
    CtRepo ctRepo;
    @Value("${itoss.manager.role.primary:false}")
    private boolean isPrimary;
    @Autowired
    private JwtUserDetailsService userDetailService;

    @PostMapping(value = "/schedule", produces = "application/json")
    ResponseEntity newOutage(@RequestBody Outage outageRequest) throws Exception {

        try {
            if (!scheduler.isStarted() && isPrimary)
                scheduler.start();
        } catch (SchedulerException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        UserAccount user = userDetailService
                .getUserAccount(SecurityContextHolder.getContext().getAuthentication().getName());

        ZonedDateTime startTime;
        ZonedDateTime endTime;
        try {
            startTime = ZonedDateTime.of(outageRequest.getStartTime(), outageRequest.getTimeZone());
            if (startTime.isBefore(ZonedDateTime.now())) {
                OutageScheduleResponse outageScheduleResponse = new OutageScheduleResponse(false,
                        "Outage startTime must be after current time");
                return ResponseEntity.badRequest().body(outageScheduleResponse);
            }
            endTime = ZonedDateTime.of(outageRequest.getEndTime(), outageRequest.getTimeZone());
            if (endTime.isBefore(startTime)) {
                OutageScheduleResponse outageScheduleResponse = new OutageScheduleResponse(false,
                        "Outage endTime must be after startTime");
                return ResponseEntity.badRequest().body(outageScheduleResponse);
            }

            Optional<Ct> ctFound = ctRepo.findById(outageRequest.getCtId());
            if (!ctFound.isPresent()) {
                OutageScheduleResponse outageScheduleResponse = new OutageScheduleResponse(false,
                        "The Ct for which the outage wants to be planned was not found.");
                return ResponseEntity.badRequest().body(outageScheduleResponse);
            }
            Ct ct = ctFound.get();


            JobDetail startJobDetail = buildJobDetail(outageRequest, "start", user, ct);

            Trigger startTrigger = buildJobTrigger(startJobDetail, startTime);


            JobDetail endJobDetail = buildJobDetail(outageRequest, "end", user, ct);
            startJobDetail.getJobDataMap().put("endJobKey", endJobDetail.getKey().getName());
            endJobDetail.getJobDataMap().put("startJobKey", startJobDetail.getKey().getName());
            Trigger endTrigger = buildJobTrigger(endJobDetail, endTime);
            scheduler.scheduleJob(startJobDetail, startTrigger);
            scheduler.scheduleJob(endJobDetail, endTrigger);


            OutageScheduleResponse startOutageScheduleResponse = new OutageScheduleResponse(true,
                    startJobDetail.getKey().getName(), startJobDetail.getKey().getGroup(),
                    "Outage Start Scheduled Successfully!");
            OutageScheduleResponse endOutageScheduleResponse = new OutageScheduleResponse(true,
                    endJobDetail.getKey().getName(), endJobDetail.getKey().getGroup(),
                    "Outage End Scheduled Successfully!");


            return ResponseEntity.ok(Arrays.asList(startOutageScheduleResponse, endOutageScheduleResponse));
        } catch (Exception ex) {
            log.info("Error scheduling outage " + ex);

            OutageScheduleResponse outageScheduleResponse = new OutageScheduleResponse(false,
                    "Error scheduling outage. Please try later!");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(outageScheduleResponse);
        }

    }


    private JobDetail buildJobDetail(Outage outageRequest, String type, UserAccount user, Ct ct) {
        JobDataMap jobDataMap = new JobDataMap();

        jobDataMap.put("notes", outageRequest.getNotes());
        jobDataMap.put("type", type);
        jobDataMap.put("ctId", outageRequest.getCtId().toString());
        jobDataMap.put("ctName", ct.getName());
        jobDataMap.put("ctType", ct.getType().getName());
        jobDataMap.put("user", user.getName());
        jobDataMap.put("userId", user.getId().toString());

        return JobBuilder.newJob(OutageJob.class).withIdentity(UUID.randomUUID().toString(), "outage-jobs")
                .withDescription(
                        "Outage " + type + " Job ctId:" + outageRequest.getCtId() + " scheduled by: " + user.getName())
                .usingJobData(jobDataMap).build();
    }

    private Trigger buildJobTrigger(JobDetail jobDetail, ZonedDateTime startAt) {
        return TriggerBuilder.newTrigger().forJob(jobDetail)
                .withIdentity(jobDetail.getKey().getName(), "outage-triggers").withDescription("outage-triggers")
                .startAt(Date.from(startAt.toInstant()))
                .withSchedule(SimpleScheduleBuilder.simpleSchedule().withMisfireHandlingInstructionFireNow()).build();
    }

    @RequestMapping(value = {"/list", "/list/{id}"})

    public ResponseEntity collectionJobs(@Nullable @PathVariable("id") Long id) throws SchedulerException {
        List<Trigger> triggers = null;
        List<JobResponse> jobs = new ArrayList<>();
        for (String groupName : scheduler.getJobGroupNames()) {

            for (JobKey jobKey : scheduler.getJobKeys(GroupMatcher.jobGroupEquals(groupName))) {

                String jobName = jobKey.getName();
                String jobGroup = jobKey.getGroup();
                String jobDescription = scheduler.getJobDetail(jobKey).getDescription();
                Long ctId = scheduler.getJobDetail(jobKey).getJobDataMap().getLong("ctId");
                // get job's trigger
                triggers = (List<Trigger>) scheduler.getTriggersOfJob(jobKey);
                Date nextFireTime = triggers.get(0).getNextFireTime();
                if (id == null || id.equals(ctId)) {
                    JobResponse jobResp = new JobResponse();
                    jobResp.setCtId(ctId);
                    jobResp.setJobDescription(jobDescription);
                    jobResp.setJobGroup(jobGroup);
                    jobResp.setJobName(jobName);
                    jobResp.setNextFireTime(nextFireTime);
                    jobResp.setUser(scheduler.getJobDetail(jobKey).getJobDataMap().getString("user"));
                    jobResp.setType(scheduler.getJobDetail(jobKey).getJobDataMap().getString("type"));
                    jobResp.setNotes(scheduler.getJobDetail(jobKey).getJobDataMap().getString("notes"));
                    jobResp.setUserId(scheduler.getJobDetail(jobKey).getJobDataMap().getString("userId"));
                    jobResp.setCtName(scheduler.getJobDetail(jobKey).getJobDataMap().getString("ctName"));
                    jobResp.setCtType(scheduler.getJobDetail(jobKey).getJobDataMap().getString("ctType"));
                    if (scheduler.getJobDetail(jobKey).getJobDataMap().getString("startJobKey") != null) {
                        jobResp.setStartJobKey(scheduler.getJobDetail(jobKey).getJobDataMap().getString("startJobKey"));
                    }
                    if (scheduler.getJobDetail(jobKey).getJobDataMap().getString("endJobKey") != null) {
                        jobResp.setEndJobKey(scheduler.getJobDetail(jobKey).getJobDataMap().getString("endJobKey"));
                    }
                    jobs.add(jobResp);
                }
            }

        }

        return ResponseEntity.ok(jobs);
    }



    public List<JobDetail> getAllJobsOfScheduler(Scheduler scheduler) throws SchedulerException {
        List<JobDetail> result = new ArrayList<JobDetail>();
        List<String> jobGroupNames = scheduler.getJobGroupNames();
        for (String groupName : jobGroupNames) {
            Set<JobKey> jobKeys = scheduler.getJobKeys(GroupMatcher.<JobKey>groupEquals(groupName));
            for (JobKey jobKey : jobKeys) {
                JobDetail jobDetail = scheduler.getJobDetail(jobKey);
                result.add(jobDetail);
            }
        }
        return result;
    }
}

@Data
class JobResponse {
    String jobName;
    String jobGroup;
    String jobDescription;
    Long ctId;
    String ctType;
    String ctName;
    Date nextFireTime;
    String userId;
    String notes;
    String type;
    String user;
    String startJobKey;
    String endJobKey;
}