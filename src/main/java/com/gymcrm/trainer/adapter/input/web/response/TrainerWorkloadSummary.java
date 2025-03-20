package com.gymcrm.trainer.adapter.input.web.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TrainerWorkloadSummary {
	private String username;
	private String fullName;
	private String specialization;
	private boolean isActive;
	private Integer currentMonthWorkload;
}
