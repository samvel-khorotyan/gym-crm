package com.gymcrm.configuration;

import com.gymcrm.user.domain.UserType;
import java.util.Map;
import java.util.Set;

public class PermissionConfig {
	public static final Map<UserType, Set<String>> ROLE_PERMISSIONS = Map
	        .of(UserType.TRAINEE,
	                Set.of("VIEW_TRAINEES", "VIEW_TRAINERS", "VIEW_TRAINERS_NOT_ASSIGNED_TO_TRAINEE",
	                        "VIEW_TRAINING_TYPES", "VIEW_TRAINEES_TRAININGS", "VIEW_TRAINER_TRAININGS"),
	                UserType.TRAINER,
	                Set.of("VIEW_TRAINEES", "VIEW_TRAINERS", "VIEW_TRAINERS_NOT_ASSIGNED_TO_TRAINEE",
	                        "VIEW_TRAINING_TYPES", "VIEW_TRAINEES_TRAININGS", "VIEW_TRAINER_TRAININGS"),
	                UserType.ADMIN,
	                Set.of("VIEW_TRAINEES", "VIEW_TRAINERS", "VIEW_TRAINERS_NOT_ASSIGNED_TO_TRAINEE",
	                        "VIEW_TRAINING_TYPES", "VIEW_TRAINEES_TRAININGS", "VIEW_TRAINER_TRAININGS",
	                        "UPDATE_TRAINEES", "UPDATE_TRAINERS", "UPDATE_TRAINEE_TRAINERS", "CREATE_TRAINING",
	                        "DELETE_TRAINEES", "UPDATE_TRAINEE_STATE", "UPDATE_TRAINER_STATE"));
}
