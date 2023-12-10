package com.example.Security.controller;

import com.example.Security.model.Worklog;
import com.example.Security.model.WorklogDTO;
import com.example.Security.service.WorklogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/worklogs")
public class WorklogController {

	@Autowired
	private WorklogService worklogService;


	@PostMapping("/{taskId}")
	public ResponseEntity<?> createWorklog(@RequestBody Worklog worklog, @PathVariable String taskId) {
		if (worklog.getLoggedTimeString() == null) {
			worklog.setLoggedTimeString("1d");
		}

		ResponseEntity<?> createdWorklog = worklogService.createWorklog(worklog, taskId);
		return ResponseEntity.created(createdWorklog.getHeaders().getLocation()).body(createdWorklog.getBody());
	}

	@PutMapping("/{id}")
	public ResponseEntity<?> updateWorklog(@PathVariable Long id,
			@RequestBody Worklog updatedWorklog) {
		return worklogService.updateWorklog(id, updatedWorklog);
	}

	
	 @GetMapping("/task/{taskId}")
	    public ResponseEntity<?> getWorklogsByTaskId(@PathVariable String taskId) {
	        try {
	            List<WorklogDTO> worklogs = worklogService.getWorklogsByTaskId(taskId);
	            return ResponseEntity.ok(worklogs);
	        } catch (ResponseStatusException e) {
	            return ResponseEntity.status(e.getStatusCode()).body(e.getReason());
	        } catch (Exception e) {
	            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred");
	        }
	    }


	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteWorklog(@PathVariable Long id) {
		return worklogService.deleteWorklog(id);
	}
}