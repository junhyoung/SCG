package com.example.gateway.filter;

import com.example.gateway.jwt.JwtValidator;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class AuthorizationHeaderFilter extends AbstractGatewayFilterFactory<AuthorizationHeaderFilter.Config> {
    @Autowired
    private JwtValidator jwtValidator;

    public AuthorizationHeaderFilter() {
        super(Config.class);
    }

    public static class Config {
        // application.yml 파일에서 지정한 filer의 Argument값을 받는 부분
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            try {
                // Authorization 헤더에서 토큰 추출
                String token = exchange.getRequest().getHeaders().get("Authorization").get(0).substring(7);   // 헤더의 토큰 파싱 (Bearer 제거)

                // 토큰을 파싱하고 사용자 정보를 추출
                Map<String, Object> tokenInfo = jwtValidator.getUserParseInfo(token);

                // 토큰에서 scope 값을 추출
                String scopes =  tokenInfo.get("scope").toString();

                // 특정 조건을 만족하는지 확인 (예: "admin" 또는 "service3"인 경우)
                if (scopes != null && (scopes.contains("admin") || scopes.contains("service3"))) {
                    // 요청에 인증 헤더 추가
                    addAuthorizationHeaders(exchange.getRequest(), tokenInfo);

                    // 필터 체인 계속 진행
                    return chain.filter(exchange);
                } else {
                    // 특정 조건을 만족하지 않으면 403 Forbidden 상태로 응답
                    exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
                    exchange.getResponse().getHeaders().add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
                    exchange.getResponse().getHeaders().add(HttpHeaders.CACHE_CONTROL, "no-store, no-cache, must-revalidate");
                    exchange.getResponse().getHeaders().add(HttpHeaders.PRAGMA, "no-cache");
                    exchange.getResponse().getHeaders().add(HttpHeaders.EXPIRES, "0");

                    // 에러 메시지를 응답 본문에 추가
                    String errorMessage = "Access denied. You do not have the required scope.";
                    byte[] errorMessageBytes = errorMessage.getBytes(StandardCharsets.UTF_8);
                    exchange.getResponse().getHeaders().setContentLength(errorMessageBytes.length);
                    DataBuffer buffer = exchange.getResponse().bufferFactory().wrap(errorMessageBytes);

                    // 응답 본문에 에러 메시지 추가
                    return exchange.getResponse().writeWith(Mono.just(buffer)).then(Mono.fromRunnable(() -> {}));
                }

            } catch (Exception e) {
                // 예외 발생 시 에러 로깅 및 처리
                log.error("AuthorizationHeaderFilter: Error processing request", e);
                exchange.getResponse().setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
                return exchange.getResponse().setComplete();
            }
        };
    }

    // 성공적으로 검증이 되었기 때문에 인증된 헤더로 요청을 변경해준다. 서비스는 해당 헤더에서 아이디를 가져와 사용한다.
    private void addAuthorizationHeaders(ServerHttpRequest request, Map<String, Object> tokenInfo) {
        request.mutate()
                .header("X-Authorization-Id", tokenInfo.get("clientId").toString())
                .build();
    }

    // 토큰 검증 요청을 실행하는 도중 예외가 발생했을 때 예외처리하는 핸들러
    @Bean
    public ErrorWebExceptionHandler tokenValidation() {
        return new JwtTokenExceptionHandler();
    }

    // 실제 토큰이 null, 만료 등 예외 상황에 따른 예외처리
    public class JwtTokenExceptionHandler implements ErrorWebExceptionHandler {
        private String getErrorCode(int errorCode) {
            return "{\"errorCode\":" + errorCode +"}";
        }

        @Override
        public Mono<Void> handle(
                ServerWebExchange exchange, Throwable ex) {
            log.error(ex.getMessage());
            int errorCode = 500;
            if (ex.getClass() == NullPointerException.class) {
                errorCode = 100;
            } else if (ex.getClass() == ExpiredJwtException.class) {
                errorCode = 200;
            }

            byte[] bytes = getErrorCode(errorCode).getBytes(StandardCharsets.UTF_8);
            DataBuffer buffer = exchange.getResponse().bufferFactory().wrap(bytes);
            return exchange.getResponse().writeWith(Flux.just(buffer));
        }
    }
}
