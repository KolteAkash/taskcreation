package com.example.Security.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import java.sql.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@Data
@NoArgsConstructor
@Table(name="Roles")
public class Role {
    @PrePersist
    protected void onCreate() {
        this.created_At = new Date(System.currentTimeMillis());
    }

    @PreUpdate
    protected void onUpdate() {
        this.updated_At =new Date(System.currentTimeMillis());
    }
    @Id
    @SequenceGenerator(
            name="roel_sequence",
            sequenceName = "roel_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.AUTO,
            generator = "roel_sequence")
    private Long id;
    private String name;
    private String description;
    private Date created_At;
    private Date updated_At;
    private String created_By;
    private String updated_By;
   @ManyToMany(mappedBy = "roles")
   @Fetch(value = FetchMode.SELECT)
   @JsonIgnore
   private Set<User> user =new HashSet<>();

    public Role(Long id, String name,String description,String created_By,String updated_By) {
        this.id = id;
        this.name = name;
        this.description=description;
        this.created_By=created_By;
        this.updated_By=updated_By;
    }
    public Role( String name) {
        this.name = name;
    }
}
