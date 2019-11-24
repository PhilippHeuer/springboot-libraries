package com.github.philippheuer.springboot.metric.core.service;

import com.github.philippheuer.springboot.collectorapi.IMetricCollector;
import com.github.philippheuer.springboot.metric.core.config.CollectorConfiguration;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.ProcessEngine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
@Slf4j
public class MetricCollectorService {

    @Autowired
    private MeterRegistry meterRegistry;

    @Autowired
    private ProcessEngine processEngine;

    @Autowired
    private CollectorConfiguration collectorConfiguration;

    /**
     * Holds a reference to each collector
     */
    private Map<String, IMetricCollector> collectorMap = new HashMap<>();

    /**
     * Tracks when a collector was last used
     */
    private Map<String, Long> lastFetched = new ConcurrentHashMap<>();

    /**
     * Collect Metrics
     */
    public void collectMetrics() {
        // get config
        List<CollectorConfiguration.CollectorConfigurationEntry> items = collectorConfiguration.getCollectors();

        // filter list
        items = items.stream().filter(item -> !lastFetched.containsKey(item) || lastFetched.containsKey(item) && lastFetched.get(item) + (item.getFrequency()) < new Date().getTime() ).collect(Collectors.toList());
        if (items.size() > 0) {
            log.info("Refreshing metrics :: " + items.size() + " - " + items.stream().map(m -> m.getName()).collect(Collectors.joining(", ")));
        }

        // for each metric entity
        items.stream()
            .parallel()
            .forEach(item -> {
                try {
                    log.debug("[{}] Collecting ...", item.getName());

                    // register collector / remove invalid
                    if (!collectorMap.containsKey(item.getName())) {
                        try {
                            Class<?> clazz = Class.forName(item.getName());

                            IMetricCollector collector = (IMetricCollector) clazz.newInstance();
                            collector.setProcessEngine(processEngine);
                            collector.setMeterRegistry(meterRegistry);

                            collectorMap.put(item.getName(), collector);
                        } catch (ClassNotFoundException ex) {
                            collectorMap.remove(item.getName());
                            log.warn("Removed invalid collector");
                            return;
                        }
                    }

                    // collect
                    collectorMap.get(item.getName()).collect();

                    // set last fetched for metric
                    lastFetched.put(item.getName(), new Date().getTime());

                    log.debug("[{}] Success!", item.getName());
                } catch (Exception ex) {
                    log.error("[{}] Failed to fetch metrics ... [{} - {}]", item.getName(), ex.getMessage(), ex.getStackTrace()[0]);
                    ex.printStackTrace();
                }
            });
    }

}
