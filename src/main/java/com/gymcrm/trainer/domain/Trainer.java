package com.gymcrm.trainer.domain;

import com.gymcrm.trainee.domain.Trainee;
import com.gymcrm.user.domain.User;
import java.util.List;
import java.util.UUID;
import javax.persistence.*;
import lombok.*;
import org.hibernate.annotations.Type;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "trainer")
public class Trainer {
	@Id
	@Type(type = "uuid-char")
	private UUID id;

	@Column(nullable = false)
	private String specialization;

	@OneToOne
	@JoinColumn(name = "user_id",nullable = false)
	private User user;

	@ManyToMany(mappedBy = "trainers")
	private List<Trainee> trainees;

	public Trainer(UUID id, String specialization, User user) {
		this.id = id;
		this.specialization = specialization;
		this.user = user;
	}
}
