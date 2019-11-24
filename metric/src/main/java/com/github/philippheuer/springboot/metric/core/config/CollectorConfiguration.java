package com.github.philippheuer.springboot.metric.core.config;

import lombok.Data;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@ConfigurationProperties(prefix = "metric")
@Data
public class CollectorConfiguration {

    @Autowired
    private Environment environment;

    @Getter
    List<CollectorConfigurationEntry> collectors = new ArrayList<>();

    @Data
    public static class CollectorConfigurationEntry {

        private String name;

        private Boolean enabled;

        private Integer startDelay;

        private Integer frequency;

    }

}
