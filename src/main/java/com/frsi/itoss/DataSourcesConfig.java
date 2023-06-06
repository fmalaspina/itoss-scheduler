package com.frsi.itoss;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

@Configuration
public class DataSourcesConfig {
    @Bean(name = "appDataSource")
    @Primary
    @ConfigurationProperties(prefix = "spring.datasource")
    public HikariDataSource primaryDataSource() {
        return DataSourceBuilder.create().type(HikariDataSource.class).build();
    }

    @Bean(name = "appJdbcTemplate")

    public NamedParameterJdbcTemplate appJdbcTemplate() {
        return new NamedParameterJdbcTemplate(primaryDataSource());
    }


    @Bean(name = "timeseriesJdbcTemplate")

    public NamedParameterJdbcTemplate timeseriesJdbcTemplate() {
        return new NamedParameterJdbcTemplate(secondaryDataSource());
    }

    @Bean(name = "timeseriesDataSource")
    @ConfigurationProperties(prefix = "metrics.timescaledb")
    public HikariDataSource secondaryDataSource() {
        return DataSourceBuilder.create().type(HikariDataSource.class).build();

    }
}
