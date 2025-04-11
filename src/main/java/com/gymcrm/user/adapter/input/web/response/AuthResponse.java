package com.gymcrm.user.adapter.input.web.response;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class AuthResponse {
	private String token;

	public static AuthResponse form(String token) {
		return new AuthResponse(token);
	}
}
