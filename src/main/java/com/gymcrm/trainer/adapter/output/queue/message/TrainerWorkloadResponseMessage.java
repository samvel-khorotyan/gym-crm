package com.gymcrm.trainer.adapter.output.queue.message;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serial;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TrainerWorkloadResponseMessage implements Serializable {
	@Serial
	private static final long serialVersionUID = 1L;

	private String username;

	@JsonProperty("firstName")
	private String firstName;

	@JsonProperty("lastName")
	private String lastName;

	@JsonProperty("isActive")
	private Boolean isActive;

	private Integer year;
	private Integer month;

	@JsonProperty("summaryDuration")
	private Integer summaryDuration;

	@JsonProperty("transactionId")
	private String transactionId;

	private Boolean error;

	@JsonProperty("errorMessage")
	private String errorMessage;
}
