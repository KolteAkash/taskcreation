package com.example.Security.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
@Table(name="epic")
public class Epic {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long epicId;

    private String epicName;

    @Column
    private String epicDescription;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "initiative")
    private Initiative initiative;
    private String jsonFilePath;
}
