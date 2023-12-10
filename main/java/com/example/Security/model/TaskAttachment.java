package com.example.Security.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Entity
@Getter
@Setter
@Data
@NoArgsConstructor
@Table(name="TaskAttachment")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class TaskAttachment {
    @PrePersist
    protected void onCreate() {
        this.created_At = new Date(System.currentTimeMillis());
    }

    @PreUpdate
    protected void onUpdate() {
        this.updated_At =new Date(System.currentTimeMillis());
    }
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int taskAttachmentId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "task_id")
    private Task task;
    private String attachmentPath;
    private Date created_At;
    private Date updated_At;
    private String createdBy;
    private String updatedBy;
}
