package com.example.Security.service;

import java.util.List;

import org.springframework.http.ResponseEntity;
import com.example.Security.model.Worklog;
import com.example.Security.model.WorklogDTO;

public interface WorklogService {

	ResponseEntity<?> createWorklog(Worklog worklog, String taskId);

	ResponseEntity<?> updateWorklog(Long id, Worklog updatedWorklog);

	ResponseEntity<Void> deleteWorklog(Long id);

	List<WorklogDTO> getWorklogsByTaskId(String taskId);

}