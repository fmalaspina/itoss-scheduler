package com.frsi.itoss.mgr.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.frsi.itoss.mgr.integration.CtPayloadDecorator;
import com.frsi.itoss.mgr.integration.DashboardEntryPayloadDecorator;
import com.frsi.itoss.mgr.integration.UserAccountDecorator;
import com.frsi.itoss.model.ct.Ct;
import com.frsi.itoss.model.repository.CtRepo;
import com.frsi.itoss.model.repository.DashboardEntryRepo;
import com.frsi.itoss.model.statemachine.CtEvent;
import com.frsi.itoss.model.statemachine.CtEventPayload;
import com.frsi.itoss.model.user.UserAccount;
import com.frsi.itoss.shared.DashboardEntry;
import com.frsi.itoss.shared.DashboardEntryKey;
import com.frsi.itoss.shared.TaskContext;
import com.frsi.itoss.shared.TaskResult;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.jayway.jsonpath.JsonPath;
import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@ConditionalOnProperty(name = "itoss.integration.script.path")
public final class TicketCreationService {


    @Autowired
    CtRepo ctRepo;
    @Autowired
    DashboardEntryRepo dashboardEntryRepo;

    @Value("${itoss.integration.timeout:10000}")

    private Long timeout;

    private int exitCode;

    @Value("${itoss.integration.script.path}")
    private String scriptFile;

    private String externalCommand;
    @Value("${itoss.integration.validExitCodes:0}")

    private String validExitCodes;

    public static String escapeMetaCharacters(String inputString) {
        String result = inputString;
        final String[] metaCharacters = {"\\", "^", "$", "{", "}", "[", "]", "(", ")", ".", "*", "+", "?", "|", "<",
                ">", "-", "&", "%"};
        //final String[] metaCharacters = { "$"};
        for (int i = 0; i < metaCharacters.length; i++) {
            if (inputString.contains(metaCharacters[i])) {
                result = result.replace(metaCharacters[i], "\\" + metaCharacters[i]);
            }
        }
        return result;
    }

    public static String replaceScriptLineParameters(String line, Ct ct, DashboardEntry de, UserAccount user) throws JsonProcessingException {
        // ${ct.user} ${tags.user} ${fields.user}

        CtPayloadDecorator ctDecorated = new CtPayloadDecorator(ct);
        DashboardEntryPayloadDecorator deDecorated = new DashboardEntryPayloadDecorator(de);
        UserAccountDecorator userDecorated = new UserAccountDecorator(user);
        String json = getJsonString(ctDecorated, deDecorated, userDecorated);
        Matcher matcher = Pattern.compile("\\$\\{(.*?)\\}").matcher(line);
        final StringBuffer b = new StringBuffer();
        var parser = JsonPath.parse(json);
        while (matcher.find()) {
            String group = "$." + matcher.group(1);
            var match = parser.read(group);
            String matchValue;

            if (match instanceof JSONArray) {
                matchValue = (String) ((JSONArray) match).toList().stream().map(e -> e.toString()).collect(Collectors.joining(","));
            } else {
                matchValue = match.toString();
            }
            matcher.appendReplacement(b, matchValue);
        }
        matcher.appendTail(b);
        return b.toString();
    }

    private static String getJsonString(CtPayloadDecorator ctDecorated,
                                        DashboardEntryPayloadDecorator deDecorated,
                                        UserAccountDecorator userDecorated) throws JsonProcessingException {
        final ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss"));
        Map<String, Object> map = new HashMap<>();
        map.put("ct", ctDecorated);
        map.put("user", userDecorated);
        map.put("dashboardEntry", deDecorated);
        String json = objectMapper.writeValueAsString(map);
        return json;
    }


    public TaskResult execute(Ct ct, DashboardEntry de, UserAccount user) throws Exception {
        //try {
        TaskResult taskResult;
        this.externalCommand = Files.lines(Path.of(scriptFile)).collect(Collectors.joining("\n"));
        //this.ct = ct;
        //this.dashboardEntry = de;
        //this.user = user;

        ThreadFactory namedThreadFactory = new ThreadFactoryBuilder()
                .setNameFormat("exec-task-thread-ticket-creation").build();
        ExecutorService executor = Executors.newSingleThreadExecutor(namedThreadFactory);

        try {
            CompletableFuture<TaskResult> cf = CompletableFuture.supplyAsync(() -> {
                try {
                    return get(ct, user, de);
                } catch (Exception e) {
                    executor.shutdown();
                    throw new CompletionException(e);
                }
            }, executor).orTimeout(timeout, TimeUnit.MILLISECONDS);

            taskResult = cf.get(timeout, TimeUnit.MILLISECONDS);


        } catch (Exception e) {

            throw e;

        } finally {
            executor.shutdown();
        }

        return taskResult;
    }

    private TaskResult get(Ct ct, UserAccount user, DashboardEntry de) throws Exception {
        Process process = this.getProcess(ct, user, de);
        TaskResult taskResult;
        //String error = readErrorBuffer(process, "TEXT");
        String out = readInputBuffer(process, "TEXT");
        process.waitFor();

        this.exitCode = process.exitValue();

        if (!this.validExitCodes.contains(String.valueOf(this.exitCode))) {
            throw new Exception(escapeMetaCharacters(out));

        } else {
            taskResult = new TaskResult(new Date(), out, "", "OK");


        }
        return taskResult;
    }

    private Process getProcess(Ct ct, UserAccount user, DashboardEntry de) throws Exception {
        boolean isWindows = System.getProperty("os.name").toLowerCase().startsWith("windows");
        String command = this.externalCommand;

        String replacedCommand = replaceScriptLineParameters(command, ct, de, user);

        ProcessBuilder builder = new ProcessBuilder();
        if (isWindows) {

            builder.command("cmd.exe", "/c", replacedCommand);
        } else {
            builder.command("sh", "-c", replacedCommand);
        }
        builder.redirectErrorStream(true);
        return builder.start();

    }

    private String readInputBuffer(Process process, String format) throws Exception {

        try (BufferedReader rdr = new BufferedReader(new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8))) {

            if (format.equals("TEXT")) {

                return rdr.lines().collect(Collectors.joining("\r\n"));
            } else {
                return rdr.lines().collect(Collectors.joining());
            }
        }

    }


    public TaskResult createTicket(Long ctId, Long monitorId, String object, CtEventPayload ctEventPayload, UserAccount user) throws Exception {
        ctEventPayload.setEvent(CtEvent.CREATE_TICKET);
        DashboardEntryKey key = new DashboardEntryKey(ctId, monitorId, object);
        Optional<DashboardEntry> optionalDe = dashboardEntryRepo.findById(key);

        Optional<Ct> ctFound = ctRepo.findById(ctId);

        TaskResult taskResult;
        if (optionalDe.isPresent() && ctFound.isPresent()) {
            DashboardEntry de = optionalDe.get();

            Ct ct = ctFound.get();
            TaskContext tc = new TaskContext();
            tc.setTaskName("Ticket Creation Task");
            //tc.setTargetId(task.getId());
            //tc.setPreviousFireTime(previousFireTime);
            //tc.setInstrumentation(task.getInstrumentation().getName());
            //tc.setInstrumentationParameters(task.getInstrumentation().getInstrumentationParameters());
            //tc.setTimeout(10000L);
//            tc.setInstrumentation("EXEC");
//            Optional<Instrumentation> optionalInst = instrumentationRepo.findById("EXEC");
//            tc.setInstrumentationParameters(optionalInst.get().getInstrumentationParameters());
//            tc.setTargetId(ctId);
//            tc.setTargetInstrumentationParameterValues(ct.getInstrumentationParameterValues());
//            tc.setMetricPayloadData(de.getMetricPayloadData());
//            ObjectMapper objectMapper = new ObjectMapper();
//            Map<String,String> ctMap = objectMapper.convertValue(ct,Map.class);
            taskResult = this.execute(ct, de, user);
            return taskResult;
        }

        throw new RuntimeException("Could not create ticket.");
    }

}
