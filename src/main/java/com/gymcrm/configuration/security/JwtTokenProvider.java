package com.gymcrm.configuration.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtTokenProvider {
	@Value("${jwt.secret}")
	private String secret;

	@Value("${jwt.expiration}")
	private long expiration;

	public String generateServiceToken() {
		Date now = new Date();
		Date expiryDate = new Date(now.getTime() + expiration);

		SecretKey key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));

		return Jwts.builder().setSubject("main-service").setIssuedAt(now).setExpiration(expiryDate).signWith(key)
		        .compact();
	}
}
