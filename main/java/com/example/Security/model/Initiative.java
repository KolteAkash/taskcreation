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
@Table(name="initiative")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "user"})
public class Initiative {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int initiative_id;
    private String initiative_name;
    private String initiative_description;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_type_id")
    private ProjectType projectType;
}
