# WebFlux Tracing Jaeger

This is a implementation for the WebFlux Tracing Module, which uses `jaeger-java``.

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
