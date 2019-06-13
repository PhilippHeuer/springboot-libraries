# WebFlux Tracing Jaeger

This is a implementation for the WebFlux Tracing Module, which uses `jaeger-java`.

## Configuration

Add the following section to your application.yml:

```yaml
opentracing:
  jaeger:
    enabled: true
    enable-b3-propagation: true
    # Sampling - Constant
    probabilistic-sampler:
      sampling-rate: 1.0
    # Sampling - RateLimited
    rate-limiting-sampler:
      max-traces-per-second: 3
    # Endpoint
    http-sender:
      url: http://localhost:14268/api/traces
    #udp-sender:
    #  host: localhost
    #  port: 6831
```

Take a look at the jaeger-spring-cloud project for all configuration options.

## Testing

### Start

```bash
docker run --name jaeger -e COLLECTOR_ZIPKIN_HTTP_PORT=9411 -p 5775:5775/udp -p 6831:6831/udp -p 6832:6832/udp -p 5778:5778 -p 16686:16686 -p 14268:14268 -p 9411:9411 jaegertracing/all-in-one:1.8
```

### Web UI

Link: http://localhost:16686/search
