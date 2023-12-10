package com.example.Security.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@Data
@NoArgsConstructor
@Table(name="Priority")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Priority {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int priority_id;
    private String priority_type;
    private String color;
}
