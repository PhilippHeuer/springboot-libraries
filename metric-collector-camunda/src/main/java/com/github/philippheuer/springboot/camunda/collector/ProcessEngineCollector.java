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
 * Collect ProcessEngine Metrics
 */
@Slf4j
public class ProcessEngineCollector implements IMetricCollector {

    @Setter
    @Getter
    private ProcessEngine processEngine;

    @Setter
    @Getter
    private MeterRegistry meterRegistry;

    List<String> processDefinitions;

    Map<String, List<String>> processDefinitionIds = new HashMap<>();

    public void collect() {
        collectJobFailed();
        collectJobSuccess();
        collectJobAcquiredFailure();
        collectJobAcquiredSuccess();
        collectJobAcquisitionAttempts();
    }

    private void collectJobFailed() {
        // tags
        List<Tag> tags = Arrays.asList(
                Tag.of("engine_name", processEngine.getName())
        );

        // count
        long count = processEngine.getManagementService().createMetricsQuery()
                .name(Metrics.JOB_FAILED)
                .sum();

        // update meter
        Meter oldGauge = meterRegistry.find("camunda_job_failed").tags(tags).meter();
        if (oldGauge != null) {
            meterRegistry.remove(oldGauge.getId());
        }
        Gauge.builder("camunda_job_failed", count, n -> n)
                .description("The total number of failed jobs.")
                .tags(tags)
                .strongReference(true)
                .register(meterRegistry);
    }

    private void collectJobSuccess() {
        // tags
        List<Tag> tags = Arrays.asList(
                Tag.of("engine_name", processEngine.getName())
        );

        // count
        long count = processEngine.getManagementService().createMetricsQuery()
                .name(Metrics.JOB_SUCCESSFUL)
                .sum();

        // update meter
        Meter oldGauge = meterRegistry.find("camunda_job_success").tags(tags).meter();
        if (oldGauge != null) {
            meterRegistry.remove(oldGauge.getId());
        }
        Gauge.builder("camunda_job_success", count, n -> n)
                .description("The total number of successful jobs.")
                .tags(tags)
                .strongReference(true)
                .register(meterRegistry);
    }

    private void collectJobAcquiredFailure() {
        // tags
        List<Tag> tags = Arrays.asList(
                Tag.of("engine_name", processEngine.getName())
        );

        // count
        long count = processEngine.getManagementService().createMetricsQuery()
                .name(Metrics.JOB_ACQUIRED_FAILURE)
                .sum();

        // update meter
        Meter oldGauge = meterRegistry.find("camunda_job_acquired_failure").tags(tags).meter();
        if (oldGauge != null) {
            meterRegistry.remove(oldGauge.getId());
        }
        Gauge.builder("camunda_job_acquired_failure", count, n -> n)
                .description("The total number of failed job acquisitions.")
                .tags(tags)
                .strongReference(true)
                .register(meterRegistry);
    }

    private void collectJobAcquiredSuccess() {
        // tags
        List<Tag> tags = Arrays.asList(
                Tag.of("engine_name", processEngine.getName())
        );

        // count
        long count = processEngine.getManagementService().createMetricsQuery()
                .name(Metrics.JOB_ACQUIRED_SUCCESS)
                .sum();

        // update meter
        Meter oldGauge = meterRegistry.find("camunda_job_acquired_success").tags(tags).meter();
        if (oldGauge != null) {
            meterRegistry.remove(oldGauge.getId());
        }
        Gauge.builder("camunda_job_acquired_success", count, n -> n)
                .description("The total number of successfully acquired jobs.")
                .tags(tags)
                .strongReference(true)
                .register(meterRegistry);
    }

    /**
     * Collect the count of executed decision elements
     */
    private void collectJobAcquisitionAttempts() {
        // tags
        List<Tag> tags = Arrays.asList(
                Tag.of("engine_name", processEngine.getName())
        );

        // count
        long count = processEngine.getManagementService().createMetricsQuery()
                .name(Metrics.JOB_ACQUISITION_ATTEMPT)
                .sum();

        // update meter
        Meter oldGauge = meterRegistry.find("camunda_job_acquisition_attempts").tags(tags).meter();
        if (oldGauge != null) {
            meterRegistry.remove(oldGauge.getId());
        }
        Gauge.builder("camunda_job_acquisition_attempts", count, n -> n)
                .description("The total number of executed decision elements.")
                .tags(tags)
                .strongReference(true)
                .register(meterRegistry);
    }

}
