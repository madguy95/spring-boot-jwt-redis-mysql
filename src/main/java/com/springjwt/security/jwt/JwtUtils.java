package com.springjwt.security.jwt;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.util.DigestUtils;

import com.springjwt.security.services.RedisService;
import com.springjwt.security.services.UserDetailsImpl;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;

@Component
public class JwtUtils {
	private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);

	@Value("${satoru.app.jwtSecret}")
	private String jwtSecret;

	@Value("${satoru.app.jwtExpirationMs}")
	private int jwtExpirationMs;

	@Autowired
	private RedisService service;

	public String generateJwtToken(Authentication authentication) {

		UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();
		Claims claims = Jwts.claims();
		String username = userPrincipal.getUsername();
		String hash = getHash(username);
		claims.put("username", username);
		claims.put("hash", hash);
		return Jwts.builder()
				.setSubject((userPrincipal.getUsername()))
				.setClaims(claims)
				.setIssuedAt(new Date())
				.setExpiration(new Date((new Date()).getTime() + jwtExpirationMs))
				.signWith(SignatureAlgorithm.HS512, jwtSecret)
				.compact();
	}

	public String getUserNameFromJwtToken(String token) {
		return (String) Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token).getBody().get("username");
	}

	public boolean revokeToken(Authentication auth, String token) {
		Claims claims = Jwts.claims();
		try {
			claims = Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token).getBody();

		} catch (Exception e) {
			return false;
		}

		// Valid token and now checking to see if the token is actally expired or alive
		// by quering in redis.
		if (claims != null && claims.containsKey("username") && claims.containsKey("hash")) {
			String username = claims.get("username").toString();
			String hash = claims.get("hash").toString();
			service.setValue(String.format("%s_%s", username, hash), auth.getPrincipal(), TimeUnit.SECONDS, 3600, true);
		}
		return true;
	}

	public boolean validateJwtToken(String authToken) {
		try {
			Claims claims = Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(authToken).getBody();
			if (claims != null && claims.containsKey("username") && claims.containsKey("hash")) {
				String username = claims.get("username").toString();
				String hash = claims.get("hash").toString();
				if (service.getValue(String.format("%s_%s", username, hash)) != null) {
					logger.error("Token revoked");
				} else {
					return true;
				}
			}
		} catch (SignatureException e) {
			logger.error("Invalid JWT signature: {}", e.getMessage());
		} catch (MalformedJwtException e) {
			logger.error("Invalid JWT token: {}", e.getMessage());
		} catch (ExpiredJwtException e) {
			logger.error("JWT token is expired: {}", e.getMessage());
		} catch (UnsupportedJwtException e) {
			logger.error("JWT token is unsupported: {}", e.getMessage());
		} catch (IllegalArgumentException e) {
			logger.error("JWT claims string is empty: {}", e.getMessage());
		}

		return false;
	}

	public String getHash(String username) {
		return DigestUtils.md5DigestAsHex(String.format("%s_%d", username, new Date().getTime()).getBytes());
	}

}
