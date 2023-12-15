package com.example.gateway.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class JwtValidator implements Serializable {

    private static final long serialVersionUID = -2550185165626007488L;
    @Value("${jwt.secret}")
    private String secret;
    private static final Logger logger = LoggerFactory.getLogger(JwtValidator.class);

    public Map<String, Object> getUserParseInfo(String token) {
        Claims parseToken = Jwts.parser().setSigningKey(secret.getBytes()).parseClaimsJws(token).getBody();
        Map<String, Object> result = new HashMap<>();
        //expiration date < now
        boolean isExpired = !parseToken.getExpiration().before(new Date());
        result.put("clientId", parseToken.getSubject());
        result.put("scope", parseToken.get("scope"));
        result.put("isExpired", isExpired);
        return result;
    }
}
