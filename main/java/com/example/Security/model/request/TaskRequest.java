package com.example.Security.model.request;

import com.example.Security.model.Priority;
import com.example.Security.model.Project;
import com.example.Security.model.ProjectStatus;
import com.example.Security.model.TaskType;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TaskRequest {
    private int project_id;
    private String task_summary;
    private String task_details;
    private String assignee;
    private String reporter;
    private int status_id;
    private int priority_id;
}
