package com.gymcrm.unit.configuration.security.auth;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

import com.gymcrm.configuration.security.auth.CustomAuthenticationProvider;
import com.gymcrm.configuration.security.protection.BruteForceProtectionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class CustomAuthenticationProviderTest {
	@Mock
	private UserDetailsService userDetailsService;

	@Mock
	private PasswordEncoder passwordEncoder;

	@Mock
	private BruteForceProtectionService bruteForceProtectionService;

	@InjectMocks
	private CustomAuthenticationProvider customAuthenticationProvider;

	private final String username = "testUser";
	private UserDetails userDetails;

	@BeforeEach
	void setUp() {
		userDetails = mock(UserDetails.class);
		when(userDetails.getUsername()).thenReturn(username);
		when(userDetails.getPassword()).thenReturn("$2a$10$somehashedpassword"); // hashed password example
	}

	@Test
	void shouldAuthenticateSuccessfullyWhenCredentialsAreCorrect() {
		String password = "password123";
		Authentication authentication = new UsernamePasswordAuthenticationToken(username, password);

		when(bruteForceProtectionService.isBlocked(username)).thenReturn(false);
		when(userDetailsService.loadUserByUsername(username)).thenReturn(userDetails);
		when(passwordEncoder.matches(password, userDetails.getPassword())).thenReturn(true);

		Authentication result = customAuthenticationProvider.authenticate(authentication);

		assertNotNull(result);
		assertEquals(username, result.getName());
		assertEquals(userDetails.getAuthorities(), result.getAuthorities());

		verify(bruteForceProtectionService, times(1)).loginSucceeded(username);
		verify(bruteForceProtectionService, never()).loginFailed(anyString());
	}
}
