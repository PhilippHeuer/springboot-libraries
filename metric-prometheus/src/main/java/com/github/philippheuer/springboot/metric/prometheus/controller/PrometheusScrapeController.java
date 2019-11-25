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
     * <p>
     * Available at /prometheus by default, set metric.exporter.prometheus.endpoint to overwrite.
     *
     * @param prefixFilter Prefix Filter
     * @return Prometheus Scrape
     */
    @RequestMapping("${metric.exporter.prometheus.endpoint:/prometheus}")
    public ResponseEntity<String> filteredPrometheusExport(
        @RequestParam(value = "prefixFilter", required = false, defaultValue = "") List<String> prefixFilter
    ) {
        String prometheusScrape = prometheusMeterRegistry.scrape();
        StringBuilder resultBuilder = new StringBuilder();

        prometheusScrape.lines().forEach(line -> {
            // do nothing if no prefixFilter was specified
            if (prefixFilter.size() > 0) {
                prefixFilter.forEach(filter -> {
                    if (line.startsWith(filter) || line.startsWith("# HELP " + filter) || line.startsWith("# TYPE " + filter)) {
                        resultBuilder.append(line + System.getProperty("line.separator"));
                    }
                });
            } else {
                resultBuilder.append(line + System.getProperty("line.separator"));
            }
        });

        return ResponseEntity.ok().body(resultBuilder.toString());
    }

}
