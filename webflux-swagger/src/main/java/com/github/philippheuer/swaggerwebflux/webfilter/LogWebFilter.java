package com.github.philippheuer.swaggerwebflux.webfilter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@Component
public class LogWebFilter implements WebFilter {

    private static final Logger LOGGER = LoggerFactory.getLogger(LogWebFilter.class);

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        long startTime = System.currentTimeMillis();
        String path = exchange.getRequest().getURI().getPath();
        LOGGER.info("Serving '{}'", path);

        return chain.filter(exchange).doAfterTerminate(() -> {
            LOGGER.info("Served '{}' as {} in {} msec", path, exchange.getResponse().getStatusCode(), System.currentTimeMillis() - startTime);
        });
    }
}
