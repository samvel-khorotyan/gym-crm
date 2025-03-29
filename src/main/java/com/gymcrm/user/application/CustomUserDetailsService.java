package com.gymcrm.user.application;

import com.gymcrm.user.application.port.output.LoadUserPort;
import com.gymcrm.user.domain.User;
import java.util.Collections;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
	private final LoadUserPort loadUserPort;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		User user = loadUserPort.findByUsername(username);

		return org.springframework.security.core.userdetails.User.builder().username(user.getUsername())
		        .password(user.getPassword())
		        .authorities(Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + user.getUserType())))
		        .build();
	}
}
