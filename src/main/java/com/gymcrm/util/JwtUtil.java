package com.gymcrm.util;

import com.gymcrm.user.application.port.output.TokenBlacklistPort;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import org.springframework.stereotype.Component;

@Component
public class JwtUtil {
	private static final String SECRET_KEY = "your_secret_key_which_should_be_at_least_32_characters";
	private static final Key key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8));

	private final TokenBlacklistPort tokenBlacklistPort;

	public JwtUtil(TokenBlacklistPort tokenBlacklistPort) {
		this.tokenBlacklistPort = tokenBlacklistPort;
	}

	public String generateToken(String username, String role) {
		return Jwts.builder().setClaims(Map.of("role", role)).setSubject(username).setIssuedAt(new Date())
		        .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60))
		        .signWith(key, SignatureAlgorithm.HS256).compact();
	}

	public String extractUsername(String token) {
		return extractClaim(token, Claims::getSubject);
	}

	public boolean validateToken(String bearerToken, String username) {
		String token = extractToken(bearerToken);
		return Objects.equals(extractUsername(token), username) && !tokenBlacklistPort.isTokenBlacklisted(token);
	}

	private String extractToken(String bearerToken) {
		if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
			return bearerToken.substring(7);
		}
		return bearerToken;
	}

	private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
		Claims claims = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
		return claimsResolver.apply(claims);
	}
}
