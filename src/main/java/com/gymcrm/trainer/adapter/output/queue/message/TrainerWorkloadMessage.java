package com.gymcrm.trainer.adapter.output.queue.message;

import com.gymcrm.trainer.domain.ActionType;
import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TrainerWorkloadMessage implements Serializable {
	@Serial
	private static final long serialVersionUID = 1L;

	private String username;
	private String firstName;
	private String lastName;
	private Boolean isActive;
	private LocalDate trainingDate;
	private Integer trainingDuration;
	private ActionType actionType;
	private String transactionId;
	private Integer year;
	private Integer month;
}
