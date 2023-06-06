package com.frsi.itoss.mgr.services;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.frsi.itoss.mgr.controllers.TaskController;
import com.frsi.itoss.mgr.health.ManagerSelfMonitorHealthService;
import com.frsi.itoss.model.ct.Ct;
import com.frsi.itoss.model.repository.CtRepo;
import com.frsi.itoss.model.repository.CtStatusRepo;
import com.frsi.itoss.model.repository.TaskLogRepo;
import com.frsi.itoss.model.repository.TaskRepo;
import com.frsi.itoss.model.task.TaskLog;
import com.frsi.itoss.shared.ManagerAction;
import com.frsi.itoss.shared.Task;
import com.frsi.itoss.shared.TaskResult;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.logging.Level;

@Service
@Log
public class TaskListener {
    @Autowired
    ManagerSelfMonitorHealthService statisticService;
    @Autowired
    ApplicationEventPublisher eventPublisher;
    @Autowired
    CtStatusRepo ctStatusRepo;
    @Autowired
    CtRepo ctRepo;

    @Autowired
    TaskRepo taskRepo;
    @Autowired
    TaskLogRepo taskLogRepo;

    @Autowired
    TaskController taskController;

    @Async("taskExecutor")
    @EventListener(condition = "#event.operation.name() == 'EXECUTE_TASK'")
    public void listenExecuteTask(ManagerAction event) {

        log.info(event.getOperation() + " " + event.getActionObject().toString());
        ObjectMapper mapper = new ObjectMapper();
        Task task = mapper.convertValue(event.getActionObject(), Task.class);
        Long start = System.currentTimeMillis();
        this.statisticService.inc("received", "execute_command");
        this.statisticService.inc("processing", "execute_command");
        Optional<Ct> optionalCt = ctRepo.findById(task.ctId());

        if (optionalCt.isPresent()) {

            Ct realCt = optionalCt.get();


            Optional<com.frsi.itoss.model.task.Task> taskFound = taskRepo.findById(task.taskId());
            if (!taskFound.isPresent()) {
                if (log.isLoggable(Level.SEVERE))
                    log.severe("Unable to find task id:" + task.taskId());
                this.statisticService.dec("processing", "execute_command");
                this.statisticService.inc("with_error", "execute_command");
                return;
            }

            try {

                var realTask = taskFound.get();
                TaskResult taskResult = taskController.getTaskRawResponse(realTask, realCt,
                        taskController.getCollectorEndpoint(Long.valueOf(task.payload().get("collectorId").toString()), realCt), task.previousFireTime());
                TaskLog taskLog = new TaskLog();
                taskLog.setStatus(taskResult.status());
                task.payload().put("colectorName", realCt.getCollector().getName());

                taskLog.setPayload(task.payload());
                taskLog.setCtId(task.ctId());
                taskLog.setFormat(realTask.getFormat());
                taskLog.setOutput(taskResult.output() + taskResult.error());
                taskLog.setTaskId(task.taskId());
                taskLog.setTaskName(realTask.getName());
                taskLog.setTimestamp(taskResult.timestamp());

                taskLogRepo.saveAndFlush(taskLog);

                log.info("Successfuly executed task: " + taskFound.get().getName());
                Long end = System.currentTimeMillis();
                Long duration = end - start;
                this.statisticService.dec("processing", "execute_command");
                this.statisticService.inc("finished", "execute_command");
                this.statisticService.setDuration("execute_command", duration);


            } catch (Exception e) {
                if (log.isLoggable(Level.SEVERE))
                    log.severe("Unable to execute task id:" + task.taskId() + " " + e.getMessage());
                this.statisticService.dec("processing", "execute_command");
                this.statisticService.inc("with_error", "execute_command");
            }
        }

    }


}

