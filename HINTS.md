# Hints

**Spoiler Alert**

We encourage you to work on the assignment yourself or together with your peers. However, situations may present themselves to you where you're stuck on a specific assignment. Thus, this document contains a couple of hints that ought to guide you through a specific task of the lab assignment.

In any case, don't hesitate to talk to us if you're stuck on a given problem!

## Task 1.2

You'll need the following dependencies:

* Gateway (Spring Cloud Routing): This adds the dependency `org.springframework.cloud:spring-cloud-starter-gateway`, which pulls in all the necessary code to run the Spring Boot application as a Spring Cloud Gateway server.
* Eureka Discovery Client (Spring Cloud Discovery): This adds the dependency `org.springframework.cloud:spring-cloud-starter-netflix-eureka-client`. As our application services, the edge service uses the discovery client component to route requests to one of the available application instances of registered applications.

For easier debuggability, we recommend to also add the following dependency:

* Spring Boot Actuator (Ops): This adds the dependency `org.springframework.boot:spring-boot-starter-actuator`

## Task 1.4

Add the following segment to the `application.yaml`:

```yaml
server:
  port: 8080
```

## Task 2.1

Use the `Method` predicate.

```yaml
spring:
  cloud:  
    gateway:  
      routes:
        - id: query-service  
          uri: http://localhost:8090  
          predicates:  
            - Method=GET
```

## Task 3.1

The difference is in their evaluation. Multiple predicates are evaluated using the AND operator. As such, the first route definition will fail to match any HTTP requests, since you cannot attribute a HTTP request with multiple HTTP methods. The second route definition is correct: It configures the `Method` predicate correctly for multiple HTTP methods that it should match on by using a comma-separated list of admissible HTTP methods.

## Task 3.2

Use the `Method` predicate.

```yaml
spring:
  cloud:  
    gateway:  
      routes:  
        - id: command-service  
          uri: http://localhost:8089  
          predicates:  
            - Method=POST,PUT,DELETE
```

## Task 4.1

The Edge Service redirects to the OpenAPI UI of the query service. This is correct: Our routing rules are simple and only look at the HTTP method of the incoming HTTP request to select their final destination. Since both the Command Service and Query Service share the same root path, we're somewhat at a loss here and need to refine our route definitions.

## Task 4.2

Of course, this could be easily remedied by introducing a proper context path for both services. But oftentimes, you can't exert that kind of control over services that you have to integrate. Another possibility is to provide the context path at the level of the Edge Service and re-write the path for the target service. You can use rewrite filters to do so.

## Task 4.3 and 4.4

* Use the `Path` predicate to match all requests with path segment `/command-service`: `Path=/command-service/**`
* Use the `RewritePath` filter to remove the `/command-service` path segment from the resulting request: `RewritePath=/command-service/(?<remaining>.*), /$\{remaining}`

The resulting route definition looks like this:

```yaml
- id: command-service  
  uri: http://localhost:8089  
  predicates:  
    - Path=/command-service/**  
  filters:  
    - RewritePath=/command-service/(?<remaining>.*), /$\{remaining}
```

Do the same for the route to the query service.

## Task 4.5

Unfortunately, we can't access the OpenAPI UI website without another change. When the OpenAPI UI is presented behind an edge server, it must be able to present an OpenAPI specification of the API that contains the correct server URL - which is the URL of the edge server instead of the URL of the service itself. To enable the service to produce the correct server URL, the following configuration has to be added to the `application.yaml` to both the Command Service and Query Service.

```yaml
server:
  forward-headers-strategy: framework
```

With this configuration in place, everything should work fine now.

## Task 5

1. The Edge Service needs to be aware of all the available application instances. Hence, it must integrate properly with the Eureka-backed Discovery Service. Answer the following questions:

* What is required to configure the Edge Service as a client that interacts with the Discovery Service? (if you're unsure, refer to the configuration that you've done in the previous lab assignment).
* How does the Edge Service target an application instance? (if you're unsure, revisit the slides on _Integrating an Eureka client_ from the session _Service Discovery with Eureka_).

2. Configure the Edge Service (cf. `application.yaml`). The Edge Service should listen on port `8080`. Provide all the necessary configuration that you identified in task 1.4.

3. The Edge Service should be able to perform _client-side load-balancing_. Which bean do you need to provide in order to enable client-side load balancing?

## Task 5.1

The Edge Service needs to know where the Discovery Service is located at. Add the following segment to its `application.yaml`.

```yaml
eureka:  
  client:  
    serviceUrl:  
      defaultZone: http://localhost:8761/eureka
```

The Edge Service should use proper naming when registering itself at the Discovery Service. Its name is provided using the `spring.application.name` configuration property. Add the following segment to the `application.yaml`.

```yaml
spring:
  application:
    name: gtd-edge-service
```

## Task 5.2

The Edge Service - as any other Eureka-enabled client - uses the standard `WebClient` bean to target an application instance.

## Task 5.3

Spring Cloud offers the `@LoadBalanced` annotation that works well in combination with `WebClient` (with both blocking and reactive implementations). Providing `WebClient.Builder` bean that is annotated with `@LoadBalanced` instructs Spring Cloud to use `WebClient` instances that are able to load-balance between all application instances of a target application that are registered with the Eureka server.

Add the following code to the main class of the service or any other class that is annotated with `@Configuration`.

```java
@Bean
@LoadBalanced
public WebClient.Builder loadBalancedWebClientBuilder() {
    return WebClient.builder();
}
```

## Task 5.4

The path to the target needs to change. We should no longer address individual hosts and ports, but use the capabilities of the Discovery Service. This means that a route definition like

```yaml
spring:
  cloud:  
    gateway:  
      routes:
        - id: query-service  
          uri: http://localhost:8090  
          predicates:  
            - Method=GET
```

needs to be re-written to this:

```yaml
spring:
  cloud:  
    gateway:  
      routes:
        - id: query-service  
          uri: lb://gtd-query-service  
          predicates:  
            - Method=GET
```

where `lb://` instructs the Edge Service to use a load-balanced `WebClient` and `gtd-query-service` is the `spring.application.name` of the Query Service. With this route definition in place, the Edge Service will route a matching request to one of the available application instances of the Query Service.

## Task 6.1

```yaml
- id: eureka-api  
  uri: http://${app.eureka-server}:8761  
  predicates:  
    - Path=/eureka/api/{segment}  
  filters:  
    - SetPath=/eureka/{segment}
```

## Task 6.2

```yaml
- id: eureka-web-start  
  uri: http://${app.eureka-server}:8761  
  predicates:  
    - Path=/eureka/web  
  filters:  
    - SetPath=/
```

## Task 6.3

```yaml
- id: eureka-web-other  
  uri: http://${app.eureka-server}:8761  
  predicates:  
    - Path=/eureka/**
```