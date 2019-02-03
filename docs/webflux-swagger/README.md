# WebFlux Swagger Module

## Installation

```java
compile 'com.github.philippheuer.springboot:springboot-webfluxswagger:version'
```
or (with UI)
```java
compile 'com.github.philippheuer.springboot:springboot-webfluxswaggerui:version'
```

Get the latest version from bintray: https://bintray.com/beta/#/philippheuer/maven/springboot-webfluxswagger?tab=overview

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
