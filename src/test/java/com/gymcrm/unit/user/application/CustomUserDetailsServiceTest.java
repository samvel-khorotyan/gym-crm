package com.gymcrm.unit.user.application;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.gymcrm.user.application.CustomUserDetailsService;
import com.gymcrm.user.application.port.output.LoadUserPort;
import com.gymcrm.user.domain.User;
import com.gymcrm.user.domain.UserType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest {
	@Mock
	private LoadUserPort loadUserPort;

	@InjectMocks
	private CustomUserDetailsService customUserDetailsService;

	@Test
	void shouldLoadUserByUsernameSuccessfully() {
		String username = "testUser";
		User mockUser = new User();
		mockUser.setUsername(username);
		mockUser.setPassword("password123");
		mockUser.setUserType(UserType.ADMIN);

		when(loadUserPort.findByUsername(username)).thenReturn(mockUser);

		UserDetails userDetails = customUserDetailsService.loadUserByUsername(username);

		assertNotNull(userDetails);
		assertEquals(username, userDetails.getUsername());
		assertEquals("password123", userDetails.getPassword());
		assertTrue(userDetails.getAuthorities().stream()
		        .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_ADMIN")));

		verify(loadUserPort, times(1)).findByUsername(username);
	}
}
