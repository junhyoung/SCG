package com.example.service1.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.time.Duration;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

@RequiredArgsConstructor
@Component
public class JwtTokenProvider {

    private String secretKey = "testscretkeytestscretkeytestscretkeytestscretkeytestscretkey";

    // 객체 초기화, secretKey를 Base64로 인코딩한다.
    @PostConstruct
    protected void init() {
        secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
    }

    // JWT 토큰 생성
    public String createToken(Map<String, String> info) {
        Date now = new Date();
        Date expiration = new Date(now.getTime() + Duration.ofDays(7).toMillis()); // 만료기간 7일

        String audience = info.get("x-api-tran-id").toString().substring(0, 10); // header 정보로 빼오는 값임.
        String clientId = info.get("client_id");
        String issuer = info.get("org_code");
        String ci = "test-ci-number";
        String scope = info.get("scope");


        // client secret은 어디에 넣는지 확인 필요
        return Jwts.builder()
                .setIssuedAt(now)               // 발급시간(iat)
                .setExpiration(expiration)      // 만료시간(exp)
                .setId("test")                  // jti: 발급 주체가 토큰을 식별할 수 있는 ID(임의지정)
                .setAudience(audience)          // 접근토큰 수신 기관코드
                .setIssuer(issuer)              // 토큰발급자(iss): issuerOrgCode // 접근토큰 발급 기관코드
                .setSubject(clientId)           // clientId
                .claim("scope", scope)    // scope
                .claim("ci", ci)          // ci
                .signWith(Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey)), SignatureAlgorithm.HS256) // 알고리즘, 시크릿 키
                .compact();

    }
}
