package com.example.Security.model.response;

import com.example.Security.model.Project;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProjectStatusResponse {
    private int project_status_id;
    private Project project;
    private List<StatusResponse> statusResponses;
    private int clientId;
}
