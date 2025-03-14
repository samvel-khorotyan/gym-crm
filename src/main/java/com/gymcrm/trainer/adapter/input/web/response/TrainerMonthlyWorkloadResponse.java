package com.gymcrm.trainer.adapter.input.web.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TrainerMonthlyWorkloadResponse {
	private String username;
	private String firstName;
	private String lastName;
	private Boolean isActive;
	private Integer year;
	private Integer month;
	private Integer summaryDuration;
}
