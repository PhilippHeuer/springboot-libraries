package com.github.philippheuer.webflux.common.webfilter;

import com.github.philippheuer.webflux.common.domain.UrlPathRewriteRule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * WebFilter: URL Path Rewrite
 */
public abstract class UrlPathRewriteFilter implements WebFilter {

    /**
     * Logging
     */
    private static final Logger LOG = LoggerFactory.getLogger(UrlPathRewriteFilter.class);

    /**
     * Rewrite Rules
     */
    private List<UrlPathRewriteRule> rewriteRules;

    /**
     * Constructor
     *
     * @param rewriteRules The rules
     */
    public UrlPathRewriteFilter(List<UrlPathRewriteRule> rewriteRules) {
        this.rewriteRules = rewriteRules;
    }

    /**
     * Filter
     */
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        // handle rewrite rules
        for(UrlPathRewriteRule rule : rewriteRules) {
            // check for match
            if (exchange.getRequest().getURI().getPath().equalsIgnoreCase(rule.getSourcePath())) {
                // mutate exchange
                LOG.trace("Rewrote [{}] to [{}] internally.", exchange.getRequest().getURI().getPath(), rule.getTargetPath());
                exchange = exchange.mutate().request(
                        exchange.getRequest().mutate()
                                .path(rule.getTargetPath())
                                .build())
                        .build();

            }
        }

        return chain.filter(exchange);
    }
}
