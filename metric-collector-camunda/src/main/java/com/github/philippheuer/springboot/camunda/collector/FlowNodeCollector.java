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
import org.camunda.bpm.engine.management.Metrics;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Collect FlowNode Metrics
 */
@Slf4j
public class FlowNodeCollector implements IMetricCollector {

    @Setter
    @Getter
    private ProcessEngine processEngine;

    @Setter
    @Getter
    private MeterRegistry meterRegistry;

    List<String> processDefinitions;

    Map<String, List<String>> processDefinitionIds = new HashMap<>();

    public void collect() {
        collectFlowNodes();
        collectExecutedDecisionElements();
    }

    /**
     * Collect the count of used flow node instances
     */
    private void collectFlowNodes() {
        // tags
        List<Tag> tags = Arrays.asList(
                Tag.of("engine_name", processEngine.getName())
        );

        // count
        long count = processEngine.getManagementService().createMetricsQuery()
                .name(Metrics.ACTIVTY_INSTANCE_START)
                .sum();

        // update meter
        Meter oldGauge = meterRegistry.find("camunda_used_flow_node_instances").tags(tags).meter();
        if (oldGauge != null) {
            meterRegistry.remove(oldGauge.getId());
        }
        Gauge.builder("camunda_used_flow_node_instances", count, n -> n)
                .description("The total number of used flow node instances.")
                .tags(tags)
                .strongReference(true)
                .register(meterRegistry);
    }

    /**
     * Collect the count of executed decision elements
     */
    private void collectExecutedDecisionElements() {
        // tags
        List<Tag> tags = Arrays.asList(
                Tag.of("engine_name", processEngine.getName())
        );

        // count
        long count = processEngine.getManagementService().createMetricsQuery()
                .name(Metrics.EXECUTED_DECISION_ELEMENTS)
                .sum();

        // update meter
        Meter oldGauge = meterRegistry.find("camunda_executed_decision_elements").tags(tags).meter();
        if (oldGauge != null) {
            meterRegistry.remove(oldGauge.getId());
        }
        Gauge.builder("camunda_executed_decision_elements", count, n -> n)
                .description("The total number of executed decision elements.")
                .tags(tags)
                .strongReference(true)
                .register(meterRegistry);
    }
}
