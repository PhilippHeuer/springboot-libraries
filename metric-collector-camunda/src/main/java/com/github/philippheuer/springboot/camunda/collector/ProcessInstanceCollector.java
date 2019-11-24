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

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Collect Process Instance Metrics
 */
@Slf4j
public class ProcessInstanceCollector implements IMetricCollector {

    @Setter
    @Getter
    private ProcessEngine processEngine;

    @Setter
    @Getter
    private MeterRegistry meterRegistry;

    public void collect() {
        collectActiveProcessInstanceCount();
    }

    /**
     * Collect Counts for Active Process instances for each Process Definition Key
     */
    private void collectActiveProcessInstanceCount() {
        List<String> definitions = processEngine.getRepositoryService()
            .createProcessDefinitionQuery()
            .list()
            .stream()
            .map(pi -> pi.getKey())
            .collect(Collectors.toList());

        definitions.forEach(definitionKey -> {
            // tags
            List<Tag> tags = Arrays.asList(
                    Tag.of("engine_name", processEngine.getName()),
                    Tag.of("process_definition_key", definitionKey)
            );

            // count
            Long count = processEngine.getRuntimeService()
                .createProcessInstanceQuery()
                .processDefinitionKey(definitionKey)
                .count();

            // update meter
            Meter oldGauge = meterRegistry.find("camunda_active_process_instance_count").tags(tags).meter();
            if (oldGauge != null) {
                meterRegistry.remove(oldGauge.getId());
            }
            Gauge.builder("camunda_active_process_instance_count", count, n -> n)
                    .description("The number of active process instances.")
                    .tags(tags)
                    .strongReference(true)
                    .register(meterRegistry);
        });
    }

}
