package com.github.philippheuer.webflux.tracing.webfilter;

import io.opentracing.Tracer;
import io.opentracing.propagation.Format;
import io.opentracing.propagation.TextMapAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

/**
 * Response TraceId Injector
 * <p>
 * Injects the trace id into the the response headers (based on the used codec)
 */
@Component
@Order
public class ResponseTraceIdInjector implements WebFilter {

    @Autowired
    Tracer tracer;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        // inject tracing information into the response headers
        Map<String, String> tracingHeaders = new HashMap<>();
        tracer.inject(tracer.activeSpan().context(), Format.Builtin.HTTP_HEADERS, new TextMapAdapter(tracingHeaders));
        exchange.getResponse().getHeaders().addAll(convertMapToMultiValueMap(tracingHeaders));

        return chain.filter(exchange);
    }

    private Map<String, String> convertHeadersToMap(HttpHeaders httpHeaders) {
        Map<String, String> map = new HashMap<>();
        httpHeaders.entrySet().forEach(entry -> map.put(entry.getKey(), entry.getValue().get(0)));
        return map;
    }

    private MultiValueMap convertMapToMultiValueMap(Map<String, String> map) {
        MultiValueMap multiValueMap = new LinkedMultiValueMap<String, String>();
        multiValueMap.setAll(map);
        return multiValueMap;
    }
}
