package com.frsi.itoss;

import com.frsi.itoss.model.ct.CtProcessor;
import com.frsi.itoss.model.ct.CtTypeProcessor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.cache.CacheAutoConfiguration;
import org.springframework.boot.autoconfigure.h2.H2ConsoleAutoConfiguration;
import org.springframework.boot.autoconfigure.jmx.JmxAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

import javax.annotation.PostConstruct;
import java.util.Locale;
import java.util.TimeZone;


@Configuration
@EnableScheduling
@EnableJpaRepositories(basePackages = "com.frsi.itoss.model.repository")
@EnableAsync
@SpringBootApplication(exclude = {JmxAutoConfiguration.class,CacheAutoConfiguration.class, H2ConsoleAutoConfiguration.class})

public class ItossManagerApplication {


    public static void main(String[] args) {

        SpringApplication.run(ItossManagerApplication.class, args);

    }

    @PostConstruct
    public void init() {
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));   // It will set UTC timezone
        Locale.setDefault(Locale.ENGLISH);
    }

    @Bean
    CtProcessor ctProcessor() {
        return new CtProcessor();
    }

    @Bean
    CtTypeProcessor ctTypeProcessor() {
        return new CtTypeProcessor();
    }

}





