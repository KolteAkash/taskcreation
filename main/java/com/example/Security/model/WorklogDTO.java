package com.example.Security.model;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class WorklogDTO {

	private Long id;
	private Double loggedTime;
	private LocalDateTime startDate;
	private String logDescription;
	private String loggedTimeString;
	private boolean flag;
	private String taskId;
	private String userEmail;

}