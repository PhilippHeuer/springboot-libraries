package com.github.philippheuer.springboot.camunda.collector;

import com.github.philippheuer.springboot.collectorapi.IMetricCollector;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.ProcessEngine;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Collect Process Instance Metrics
 */
@Slf4j
public class IncidentCollector implements IMetricCollector {

    @Setter
    @Getter
    private ProcessEngine processEngine;

    @Setter
    @Getter
    private MeterRegistry meterRegistry;

    List<String> processDefinitions;

    Map<String, List<String>> processDefinitionIds = new HashMap<>();

    public void collect() {
        // collect definition keys
        processDefinitions = processEngine.getRepositoryService()
                .createProcessDefinitionQuery()
                .list()
                .stream()
                .map(pi -> pi.getKey())
                .collect(Collectors.toList());

        // collect definition ids
        processDefinitionIds.clear();
        processEngine.getRepositoryService()
                .createProcessDefinitionQuery()
                .list()
                .stream()
                .forEach(pi -> {
                    if (!processDefinitionIds.containsKey(pi.getKey())) {
                        processDefinitionIds.put(pi.getKey(), new ArrayList<>());
                    }
                    processDefinitionIds.get(pi.getKey()).add(pi.getId());
                });

        collectOpenIncidentCount();
    }

    /**
     * Collect the count of currently open incidents
     */
    private void collectOpenIncidentCount() {
        processDefinitions.forEach(definitionKey -> {
            // tags
            List<Tag> tags = Arrays.asList(
                    Tag.of("engine_name", processEngine.getName()),
                    Tag.of("process_definition_key", definitionKey)
            );

            // count
            Long count = 0L;
            for (String definitionId : processDefinitionIds.get(definitionKey)) {
                count += processEngine.getRuntimeService()
                        .createIncidentQuery()
                        .processDefinitionId(definitionId)
                        .count();
            }

            // update meter
            Meter oldGauge = meterRegistry.find("camunda_incident_count").tags(tags).meter();
            if (oldGauge != null) {
                meterRegistry.remove(oldGauge.getId());
            }
            Gauge.builder("camunda_incident_count", count, n -> n)
                    .description("The number of incidents currently open.")
                    .tags(tags)
                    .strongReference(true)
                    .register(meterRegistry);
        });
    }

}
