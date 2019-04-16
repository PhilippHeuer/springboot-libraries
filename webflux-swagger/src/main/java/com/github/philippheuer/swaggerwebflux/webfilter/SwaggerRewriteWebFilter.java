package com.github.philippheuer.swaggerwebflux.webfilter;

import com.github.philippheuer.webflux.common.domain.UrlPathRewriteRule;
import com.github.philippheuer.webflux.common.webfilter.UrlPathRewriteFilter;
import org.springframework.stereotype.Component;

import java.util.Arrays;

/**
 * SwaggerRewriteWebFilter
 * <p>
 * Rewrites api-docs.json to /v2/api-docs
 */
@Component
public class SwaggerRewriteWebFilter extends UrlPathRewriteFilter {

    /**
     * Constructor
     */
    public SwaggerRewriteWebFilter() {
        super(
                Arrays.asList(
                        new UrlPathRewriteRule("/api-docs.json", "/v2/api-docs")
                )
        );
    }

}
