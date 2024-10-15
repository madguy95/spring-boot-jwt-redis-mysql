package com.springjwt.security.jwt;

import com.springjwt.security.services.RedisService;
import com.springjwt.security.services.UserDetailsImpl;
import io.jsonwebtoken.*;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class JwtUtils {

    private SecretKey SECRET_KEY;

    @Value("${app.jwtSecret}")
    private String jwtSecret;

    @Value("${app.jwtExpirationMs}")
    private int jwtExpirationMs;

    @Autowired
    private RedisService service;

    @PostConstruct
    public void generateSignInKey() {
        this.SECRET_KEY = new SecretKeySpec(Base64.getDecoder().decode(jwtSecret), "HmacSHA256");
    }

    public String parseJwt(Object request) {
        String headerAuth = null;
        if (request instanceof HttpServletRequest) {
            headerAuth = ((HttpServletRequest) request).getHeader("Authorization");
        }
        if (request instanceof StompHeaderAccessor) {
            List<String> authorization = ((StompHeaderAccessor) request).getNativeHeader("Authorization");
            if (!CollectionUtils.isEmpty(authorization)) {
                headerAuth = authorization.get(0);
            }
        }
        if (StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer ")) {
            return headerAuth.substring(7);
        }

        return headerAuth;
    }

    public String generateJwtToken(Authentication authentication) {

        UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();

        String username = userPrincipal.getUsername();
        String hash = getHash(username);
        Claims claims = Jwts.claims().add("username", username).add("hash", hash).build();
        return Jwts.builder()
                .subject((userPrincipal.getUsername()))
                .claims(claims)
                .issuedAt(new Date())
                .expiration(new Date((new Date()).getTime() + jwtExpirationMs))
                .signWith(SECRET_KEY)
                .compact();
    }

    public String getUserNameFromJwtToken(String token) {
        return (String) Jwts.parser().verifyWith(SECRET_KEY)
                .build().parseSignedClaims(token).getPayload().get("username");
    }

    public boolean revokeToken(Authentication auth, String token) {
        Claims claims;
        LocalDateTime now = LocalDateTime.now();
        try {
            claims = Jwts.parser().verifyWith(SECRET_KEY)
                    .build().parseSignedClaims(token).getPayload();

        } catch (Exception e) {
            return false;
        }

        // Valid token and now checking to see if the token is actally expired or alive
        // by quering in redis.
        if (claims != null && claims.containsKey("username") && claims.containsKey("hash")) {
            String username = claims.get("username").toString();
            String hash = claims.get("hash").toString();
            Duration aliveTime = Duration.between(now, claims.getExpiration().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime());
            service.setValue(String.format("%s_%s", username, hash), auth.getPrincipal(), TimeUnit.SECONDS, aliveTime.getSeconds(), true);
        }
        return true;
    }

    public boolean validateJwtToken(String authToken) {
        try {
            Claims claims = Jwts.parser().verifyWith(SECRET_KEY)
                    .build().parseSignedClaims(authToken).getPayload();
            if (claims != null && claims.containsKey("username") && claims.containsKey("hash")) {
                String username = claims.get("username").toString();
                String hash = claims.get("hash").toString();
                if (service.getValue(String.format("%s_%s", username, hash)) != null) {
                    log.error("Token revoked");
                } else {
                    return true;
                }
            }
        } catch (MalformedJwtException e) {
            log.error("Invalid JWT token: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            log.error("JWT token is expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            log.error("JWT token is unsupported: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            log.error("JWT claims string is empty: {}", e.getMessage());
        }
        return false;
    }

    public String getHash(String username) {
        return DigestUtils.md5DigestAsHex(String.format("%s_%d", username, new Date().getTime()).getBytes());
    }

}
