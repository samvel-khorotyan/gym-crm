package com.gymcrm.user.adapter.input.web.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.gymcrm.user.application.port.input.UpdatePasswordCommand;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginUpdateRequest {
	@NotBlank
	@Size(min = 1,max = 200)
	private String username;

	@NotBlank
	@Size(min = 1,max = 200)
	@JsonProperty("old_password")
	private String oldPassword;

	@NotBlank
	@Size(min = 1,max = 200)
	@JsonProperty("new_password")
	private String newPassword;

	public UpdatePasswordCommand toCommand() {
		return new UpdatePasswordCommand(username, oldPassword, newPassword);
	}
}
