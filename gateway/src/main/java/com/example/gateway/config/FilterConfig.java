package com.example.gateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

//@Configuration
public class FilterConfig {

//    @Bean
    public RouteLocator gatewayRoutes(RouteLocatorBuilder builder) {
        return builder.routes()
                .route(r -> r.path("/service1/**")   // r 이라는 값이 전달되면 path를 확인하고 filter를 적용해서 Uri로 이동시켜준다.
                            .filters(f -> f.addRequestHeader("first-request","first-request-header")
                                          .addResponseHeader("first-response","first-response-header"))
                            .uri("http://localhost:9001"))
                .route(r -> r.path("/service3/**")
                        .filters(f -> f.addRequestHeader("second-request","second-request-header")
                                .addResponseHeader("second-response","second-response-hㅉㅈeader"))
                        .uri("http://localhost:9003")) // route 정보
                .build(); // route 정보 메모리 반영
    }
}
