package com.example.Security.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Worklog {
	
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @PositiveOrZero
    private double loggedTime;
    
    @NotNull
    private LocalDateTime startDate;

    @Size(max = 5000)
    private String logDescription;

    @Pattern(regexp = "^[0-9]+[hwdmyn]?$")
    private String loggedTimeString;

    private boolean flag;
    
    @ManyToOne
    @JoinColumn(name = "task_id")
    private Task task;
    
    private String userEmail;
}
