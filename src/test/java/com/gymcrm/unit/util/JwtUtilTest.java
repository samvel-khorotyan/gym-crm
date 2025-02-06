package com.gymcrm.unit.util;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.gymcrm.user.application.port.output.TokenBlacklistPort;
import com.gymcrm.util.JwtUtil;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class JwtUtilTest {
	private static final String SECRET_KEY = "your_secret_key_which_should_be_at_least_32_characters";
	private static final Key key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8));

	@Mock
	private TokenBlacklistPort tokenBlacklistPort;

	@InjectMocks
	private JwtUtil jwtUtil;

	private String validToken;
	private final String username = "testUser";
	private final String role = "USER";

	@BeforeEach
	void setUp() {
		validToken = Jwts.builder().setClaims(Map.of("role", role)).setSubject(username).setIssuedAt(new Date())
		        .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60)) // 1 ժամ հետո կլրանա
		        .signWith(key).compact();
	}

	@Test
	void shouldGenerateValidToken() {
		String token = jwtUtil.generateToken(username, role);

		assertNotNull(token);
		assertFalse(token.isEmpty());
		assertEquals(username, jwtUtil.extractUsername(token));
	}

	@Test
	void shouldExtractUsernameCorrectly() {
		String extractedUsername = jwtUtil.extractUsername(validToken);

		assertEquals(username, extractedUsername);
	}

	@Test
  void shouldValidateTokenSuccessfully() {
    when(tokenBlacklistPort.isTokenBlacklisted(validToken)).thenReturn(false);

    boolean isValid = jwtUtil.validateToken("Bearer " + validToken, username);

    assertTrue(isValid);
    verify(tokenBlacklistPort, times(1)).isTokenBlacklisted(validToken);
  }

	@Test
  void shouldReturnFalseWhenTokenIsBlacklisted() {
    when(tokenBlacklistPort.isTokenBlacklisted(validToken)).thenReturn(true);

    boolean isValid = jwtUtil.validateToken("Bearer " + validToken, username);

    assertFalse(isValid);
    verify(tokenBlacklistPort, times(1)).isTokenBlacklisted(validToken);
  }

	@Test
	void shouldExtractTokenCorrectlyFromBearerString() {
		String bearerToken = "Bearer " + validToken;

		String extractedToken = jwtUtil.validateToken(bearerToken, username) ? validToken : null;

		assertNotNull(extractedToken);
		assertEquals(validToken, extractedToken);
	}
}
