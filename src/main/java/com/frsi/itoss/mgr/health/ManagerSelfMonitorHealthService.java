package com.frsi.itoss.mgr.health;

import com.frsi.itoss.CollectorFilter;
import com.frsi.itoss.model.repository.CollectorRepo;
import com.zaxxer.hikari.HikariDataSource;
import lombok.Data;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.info.BuildProperties;
import org.springframework.boot.jdbc.metadata.HikariDataSourcePoolMetadata;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@Data
@Log

public class ManagerSelfMonitorHealthService {


    private final HikariDataSource dataSource;
    private final NamedParameterJdbcTemplate appJdbcTemplate;
    private final NamedParameterJdbcTemplate tsJdbcTemplate;
    private final ThreadPoolTaskExecutor taskExecutor;
    private final HikariDataSource tsDataSource;
    BuildProperties buildProperties;
    @Value("${itoss.logging.file:/app/itoss/itoss-manager/logs/itoss-manager.log}")
    String logFile;
    DateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
    @Value("${spring.datasource.username}")
    private String username;
    private final CollectorRepo collectorRepo;
    private ConcurrentHashMap<String, Object> stats;

    private final CollectorFilter collectorFilter;
    public ManagerSelfMonitorHealthService(BuildProperties buildProperties,
                                           @Qualifier("taskExecutor") ThreadPoolTaskExecutor taskExecutor,
                                           @Qualifier("appDataSource") HikariDataSource dataSource,
                                           @Qualifier("timeseriesDataSource") HikariDataSource tsDataSource,
                                           @Qualifier("timeseriesJdbcTemplate") NamedParameterJdbcTemplate tsJdbcTemplate,
                                           @Qualifier("appJdbcTemplate") NamedParameterJdbcTemplate appJdbcTemplate,
                                           @Qualifier("collectorFilter") CollectorFilter collectorFilter, CollectorRepo collectorRepo) {
        this.dataSource = dataSource;
        this.tsDataSource = tsDataSource;
        this.appJdbcTemplate = appJdbcTemplate;
        this.tsJdbcTemplate = tsJdbcTemplate;
        this.taskExecutor = taskExecutor;
        this.buildProperties = buildProperties;
        this.stats = new ConcurrentHashMap<>();
        this.collectorFilter = collectorFilter;
        this.collectorRepo = collectorRepo;
    }






    @PostConstruct
    void init() {

        var dateTime = format.format(Date.from(buildProperties.getTime()));
        //this.resetWatch();

        var map = new LinkedHashMap<String, Object>();
        map.put("name", buildProperties.getName());
        map.put("version", buildProperties.getVersion());
        map.put("time", dateTime);
        map.put("artifact", buildProperties.getArtifact());
        map.put("group", buildProperties.getGroup());

        stats.put("build", map);
       // stats.put("start_time", format.format(new Date()));

    }

    public List<CollectorsHealth> getCollectorInfo() {
        var result = collectorFilter.getCollectorHealthList();
        result.stream().forEach((colHealth) -> {
            // Calculate the difference between timestamps and now
            var lastConfigRequestTimestamp = colHealth.getMetrics().getLast_config_request_timestamp();
            var lastMessageReceivedTimestamp = colHealth.getMetrics().getLast_message_received_timestamp();
            var now = LocalDateTime.now();
            // calculate seconds until timestamp if not null, set max_value if not.
            var lastConfigRequestSeconds = lastConfigRequestTimestamp != null ? lastConfigRequestTimestamp.until(now, ChronoUnit.SECONDS) : Long.MAX_VALUE;
            var lastMessageReceivedSeconds = lastMessageReceivedTimestamp != null ? lastMessageReceivedTimestamp.until(now, ChronoUnit.SECONDS) : Long.MAX_VALUE;
            colHealth.getMetrics().setLast_config_request_seconds(lastConfigRequestSeconds);
            colHealth.getMetrics().setLast_message_received_seconds(lastMessageReceivedSeconds);
        });
        return result;
    }


    public Map<String, Object> getLogInfo() {
        int count_warning = 0;
        int count_error = 0;
        int count_fatal = 0;
        int count_info = 0;

        var result = new LinkedHashMap<String, Object>();
        try (BufferedReader br = new BufferedReader(new FileReader(logFile))) {

            String line;
            while ((line = br.readLine()) != null) {

                if (line.contains("FATAL")) {
                    count_fatal++;
                } else {
                    if (line.contains("ERROR")) {
                        count_error++;
                    } else {

                        if (line.contains("WARN")) {
                            count_warning++;
                        } else {
                            if (line.contains("INFO")) {
                                count_info++;
                            }

                        }
                    }
                }
            }
        } catch (IOException ignore) {

        }
        result.put("info", count_info);
        result.put("fatal", count_fatal);
        result.put("error", count_error);
        result.put("warning", count_warning);
        return result;
    }


    public Map<String, Object> getThreadInfo() {

        var result = new LinkedHashMap<String, Object>();

        result.put("pool_size", taskExecutor.getPoolSize());
        result.put("active", taskExecutor.getActiveCount());
        result.put("max_pool_size", taskExecutor.getMaxPoolSize());
        result.put("core_pool_size", taskExecutor.getCorePoolSize());
        result.put("queue_capacity", taskExecutor.getQueueCapacity());
        result.put("queue_size", taskExecutor.getThreadPoolExecutor().getQueue().size());
        result.put("largest_pool_size", taskExecutor.getThreadPoolExecutor().getLargestPoolSize());


        return result;
    }

    public Map<String, Object> getAllInfo() {

        stats.put("thread", getThreadInfo());
        stats.put("timeseries_datasource", getTsDBInfo());
        stats.put("primary_datasource", getDBInfo());
        stats.put("log", getLogInfo());
        stats.put("jmx",getJmx());
        stats.put("collectors_health", getCollectorInfo());



        return stats;
    }


    private Map<String,Object> getJmx() {
        var result = new LinkedHashMap<String,Object>();

        MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();

        result.put("initial_memory",String.format("%.2f GB",
                (double)memoryMXBean.getHeapMemoryUsage().getInit() /1073741824));
        result.put("used_heap_memory",String.format("%.2f GB",
                (double)memoryMXBean.getHeapMemoryUsage().getUsed() /1073741824));
        result.put("max_heap_memory",String.format("%.2f GB",
                (double)memoryMXBean.getHeapMemoryUsage().getMax() /1073741824));
        result.put("commited_memory",String.format("%.2f GB",
                (double)memoryMXBean.getHeapMemoryUsage().getCommitted() /1073741824));

        result.put("uptime_seconds",ManagementFactory.getRuntimeMXBean().getUptime()/1000);


        return result;
    }

    @SuppressWarnings("unchecked")
    public synchronized void inc(String key, String parent) {
        Map<String, Object> map = (Map<String, Object>) stats.getOrDefault(parent, new LinkedHashMap<String, Object>());
        map.computeIfPresent(key, (k, v) -> (Long) v + 1L);
        map.putIfAbsent(key, 1L);
        stats.put(parent, map);
    }
    @SuppressWarnings("unchecked")
    public synchronized void dec(String key, String parent) {
        Map<String, Object> map = (Map<String, Object>) stats.getOrDefault(parent, new LinkedHashMap<String, Object>());

        map.computeIfPresent(key, (k, v) -> (Long) v - 1L);
        map.putIfAbsent(key, 0L);
        stats.put(parent, map);
    }
    @SuppressWarnings("unchecked")
    public synchronized void setDuration(String parent, Long duration) {
        Map<String, Object> map = (Map<String, Object>) stats.getOrDefault(parent, new LinkedHashMap<String, Object>());

        map.computeIfPresent("max_duration", (k, v) -> Long.max((Long) v, duration));
        map.computeIfAbsent("max_duration", v -> duration);
        map.computeIfPresent("min_duration", (k, v) -> Long.min((Long) v, duration));
        map.computeIfAbsent("min_duration", v -> duration);

        map.computeIfPresent("total_duration", (k, v) -> Long.sum((Long) v, duration));
        map.computeIfAbsent("total_duration", v -> duration);

        long avgDuration = Long.divideUnsigned((Long) map.getOrDefault("total_duration", 0L), (Long) map.getOrDefault("finished", 1L));
        map.computeIfPresent("avg_duration", (k, v) -> avgDuration);
        map.putIfAbsent("avg_duration", avgDuration);
        stats.put(parent, map);
    }


    public Map<String, Object> getDBInfo() {
        var result = new LinkedHashMap<String, Object>();
        try {
            var tempTemplate = appJdbcTemplate.getJdbcTemplate();
            tempTemplate.setQueryTimeout(2000);
            tempTemplate.queryForList("SELECT 1");
            result.put("status", "UP");
        } catch (Exception e) {
            result.put("status", "DOWN");
        }
        var tsDataSourcePool = new HikariDataSourcePoolMetadata(this.dataSource);
        var tsConnMax = tsDataSourcePool.getMax();
        var tsConnTimeout = this.dataSource.getConnectionTimeout();


        var tsConnActive = Optional.ofNullable(tsDataSourcePool.getActive()).orElse(-1);
        var tsConnIdle = Optional.ofNullable(tsDataSourcePool.getIdle()).orElse(-1);

        result.put("connections_max", tsConnMax);
        result.put("connections_active", tsConnActive);
        result.put("connections_idle", tsConnIdle);
        result.put("connection_timeout", tsConnTimeout);

        return result;
    }

    public Map<String, Object> getTsDBInfo() {
        var result = new LinkedHashMap<String, Object>();
        try {
            var tempTemplate = tsJdbcTemplate.getJdbcTemplate();
            tempTemplate.setQueryTimeout(2000);
            tempTemplate.queryForList("SELECT 1");
            result.put("status", "UP");
        } catch (Exception e) {
            result.put("status", "DOWN");
        }
        var tsDataSourcePool = new HikariDataSourcePoolMetadata(this.tsDataSource);
        var tsConnMax = tsDataSourcePool.getMax();
        var tsConnActive = Optional.ofNullable(tsDataSourcePool.getActive()).orElse(-1);
        var tsConnIdle = Optional.ofNullable(tsDataSourcePool.getIdle()).orElse(-1);

        result.put("connections_max", tsConnMax);
        result.put("connections_active", tsConnActive);
        result.put("connections_idle", tsConnIdle);

        return result;
    }


}
