package com.example.Security.model.response;

import com.example.Security.model.Status;
import com.example.Security.model.Task;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StatusResponse {
    private int status_id;
    private Status status;
    private List<Task> tasks;
}
