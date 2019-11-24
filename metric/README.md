# SpringBoot Micrometer Starter

## Configuration Options

```yaml
# Metric Collector
metric:
  enabled: true
  exporter:
    prometheus:
      enabled: true
  collectors:
  # Camunda Collectors
  - name: com.github.philippheuer.springboot.camunda.collector.ProcessInstanceCollector
    enabled: true
    startDelay: 0
    frequency: 15000
  - name: com.github.philippheuer.springboot.camunda.collector.IncidentCollector
    enabled: true
    startDelay: 0
    frequency: 15000
  - name: com.github.philippheuer.springboot.camunda.collector.FlowNodeCollector
    enabled: true
    startDelay: 0
    frequency: 15000
  - name: com.github.philippheuer.springboot.camunda.collector.ProcessEngineCollector
    enabled: true
    startDelay: 0
    frequency: 15000
```
