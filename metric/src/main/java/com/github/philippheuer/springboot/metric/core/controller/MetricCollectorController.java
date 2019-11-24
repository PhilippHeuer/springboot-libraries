package com.github.philippheuer.springboot.metric.core.controller;

import com.github.philippheuer.springboot.metric.core.service.MetricCollectorService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;

import java.util.Calendar;

@Controller
@Slf4j
public class MetricCollectorController {

    @Autowired
    MetricCollectorService metricCollectorService;

    @Scheduled(initialDelay = 5000, fixedDelay = 5000)
    public void collectMetrics() {
        log.debug("Collecting Metrics :: " + Calendar.getInstance().getTime());

        metricCollectorService.collectMetrics();
    }
}
