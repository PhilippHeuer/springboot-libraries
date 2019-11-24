package com.github.philippheuer.springboot.collectorapi;

import io.micrometer.core.instrument.MeterRegistry;
import org.camunda.bpm.engine.ProcessEngine;

public interface IMetricCollector {

    void setProcessEngine(ProcessEngine processEngine);

    ProcessEngine getProcessEngine();

    void setMeterRegistry(MeterRegistry meterRegistry);

    MeterRegistry getMeterRegistry();

    void collect();

}
