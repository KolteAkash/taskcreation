package com.example.Security.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.UUID;

@Entity
@Getter
@Setter
@Data
@NoArgsConstructor
@Table(name="Task")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Task {
    @PrePersist
    protected void onCreate() {
        this.created_At = new Date(System.currentTimeMillis());
    }

    @PreUpdate
    protected void onUpdate() {
        this.updated_At =new Date(System.currentTimeMillis());
    }
    @Id
    private String id= UUID.randomUUID().toString();
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id")
    private Project project;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "task_type")
    private TaskType taskType;
    private String task_summary;
    private String task_details;
    private Date start_time;
    private Date end_time;
    private Date created_At;
    private String created_By;
    private Date updated_At;
    private String updated_By;
    private String assignee;
    private String reporter;
    private Integer ClientId;
    @ManyToOne(fetch = FetchType.EAGER  )
    @JoinColumn(name = "projectStatus")
    private ProjectStatus projectStatus;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "priority")
    private Priority priority;
}
