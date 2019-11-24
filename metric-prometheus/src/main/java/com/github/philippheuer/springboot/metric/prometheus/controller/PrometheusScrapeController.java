package com.github.philippheuer.springboot.metric.prometheus.controller;

import io.micrometer.prometheus.PrometheusMeterRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Slf4j
public class PrometheusScrapeController {

    @Autowired
    private PrometheusMeterRegistry prometheusMeterRegistry;

    /**
     * Prometheus Scraper
     *
     * @param prefixFilter
     * @return
     */
    @RequestMapping("/prometheus")
    public ResponseEntity<String> filteredPrometheusExport(
        @RequestParam(value = "prefixFilter", required = false, defaultValue = "") List<String> prefixFilter
    ) {
        String prometheusScrape = prometheusMeterRegistry.scrape();
        StringBuilder resultBuilder = new StringBuilder();

        prometheusScrape.lines().forEach(line -> {
            prefixFilter.forEach(filter -> {
                if (line.startsWith(filter) || line.startsWith("# HELP " + filter) || line.startsWith("# TYPE " + filter)) {
                    resultBuilder.append(line + System.getProperty("line.separator"));
                }
            });
        });

        return ResponseEntity.ok().body(resultBuilder.toString());
    }

}
