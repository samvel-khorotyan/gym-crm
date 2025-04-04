package com.gymcrm.user.adapter.input.web.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {
	@NotBlank
	@Size(min = 1,max = 200)
	private String username;

	@NotBlank
	@Size(min = 1,max = 200)
	private String password;
}
