package com.github.philippheuer.springboot.metric.prometheus.controller;

import io.micrometer.prometheus.PrometheusMeterRegistry;
import io.prometheus.client.exporter.common.TextFormat;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
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
    @RequestMapping(
            value = "${metric.exporter.prometheus.endpoint:/metrics/prometheus}",
            produces = {io.prometheus.client.exporter.common.TextFormat.CONTENT_TYPE_004}
            )
    public ResponseEntity<String> filteredPrometheusExport(
        @RequestParam(value = "prefixFilter", required = false, defaultValue = "") List<String> prefixFilter
    ) {
        // generate metrics
        String prometheusScrape = "";
        try {
            Writer writer = new StringWriter();
            TextFormat.write004(writer, this.prometheusMeterRegistry.getPrometheusRegistry().metricFamilySamples());
            prometheusScrape = writer.toString();
        } catch (IOException ex) {
            // This actually never happens since StringWriter::write() doesn't throw any IOException
            throw new RuntimeException("Writing metrics failed", ex);
        }

        // filter metrics
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
