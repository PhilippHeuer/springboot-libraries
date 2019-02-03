# WebFlux Swagger Module

## Installation

```java
compile 'com.github.philippheuer.springboot:springboot-webfluxswagger:0.0.5'
```
or (with UI)
```java
compile 'com.github.philippheuer.springboot:springboot-webfluxswaggerui:0.0.5'
```

## Usage

Add the following import to your application class.

```java
@Import(WebFluxSwaggerModule.class)
```

## Configuration

Provide the following values, they will be used in swagger to configure the displayed properties.
```yaml
service:
  name: test
  description: test
  version: 1.0
```
