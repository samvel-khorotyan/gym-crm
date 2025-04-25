package com.gymcrm.configuration.security.auth;

import com.gymcrm.configuration.security.protection.BruteForceProtectionService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CustomAuthenticationProvider implements AuthenticationProvider {
	private final UserDetailsService userDetailsService;
	private final PasswordEncoder passwordEncoder;
	private final BruteForceProtectionService bruteForceProtectionService;

	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		String username = authentication.getName();
		String password = authentication.getCredentials().toString();

		if (bruteForceProtectionService.isBlocked(username)) {
			throw new LockedException("User is blocked for 5 minutes due to multiple failed login attempts.");
		}

		try {
			UserDetails userDetails = userDetailsService.loadUserByUsername(username);

			if (passwordEncoder.matches(password, userDetails.getPassword())) {
				bruteForceProtectionService.loginSucceeded(username);
				return new UsernamePasswordAuthenticationToken(userDetails, password, userDetails.getAuthorities());
			} else {
				bruteForceProtectionService.loginFailed(username);
				throw new BadCredentialsException("Invalid username or password.");
			}
		} catch (UsernameNotFoundException e) {
			throw new BadCredentialsException("Invalid username or password.");
		}
	}

	@Override
	public boolean supports(Class<?> authentication) {
		return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
	}
}
