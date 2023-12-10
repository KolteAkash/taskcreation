package com.example.Security.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.example.Security.model.Worklog;
import com.example.Security.model.WorklogDTO;
import com.example.Security.model.Task;
import com.example.Security.repository.WorklogRepository;
import com.example.Security.repository.TaskRepository;
import com.example.Security.service.WorklogService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;

@Service
public class WorklogServiceImpl implements WorklogService {
	@Autowired
	private WorklogRepository worklogRepository;

	@Autowired
	private TaskRepository taskRepository;

	@Override
	public List<WorklogDTO> getWorklogsByTaskId(String taskId) {
		List<Worklog> worklogs = worklogRepository.findByTaskId(taskId);

		for (Worklog worklog : worklogs) {
			isWithinCurrentTimeFrame(worklog);
		}
		worklogRepository.saveAll(worklogs);

		if (worklogs.isEmpty()) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Task not found with ID: " + taskId);
		}

		return worklogs.stream().map(this::mapToDTO).collect(Collectors.toList());
	}

	private WorklogDTO mapToDTO(Worklog worklog) {
		WorklogDTO worklogDTO = new WorklogDTO();
		worklogDTO.setId(worklog.getId());
		worklogDTO.setLoggedTime(worklog.getLoggedTime());
		worklogDTO.setStartDate(worklog.getStartDate());
		worklogDTO.setLogDescription(worklog.getLogDescription());
		worklogDTO.setLoggedTimeString(worklog.getLoggedTimeString());
		worklogDTO.setFlag(worklog.isFlag());
		worklogDTO.setTaskId(worklog.getTask().getId());
		worklogDTO.setUserEmail(worklog.getUserEmail());
		return worklogDTO;
	}

	@Override
	public ResponseEntity<Void> deleteWorklog(Long id) {
		if (worklogRepository.existsById(id)) {
			worklogRepository.deleteById(id);
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		} else {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Worklog not found");
		}
	}

	@Transactional
	@Override
	public ResponseEntity<?> createWorklog(Worklog worklog, String taskId) {
	    try {
	        validateAndAdjustWorklog(worklog);

	        Optional<Task> taskOptional = taskRepository.findById(taskId);
	        if (taskOptional.isPresent()) {
	            Task task = taskOptional.get();
	            String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
	            worklog.setUserEmail(userEmail);
	            worklog.setTask(task);

	            Worklog createdWorklog = worklogRepository.save(worklog);

	            WorklogDTO worklogDTO = mapToDTO(createdWorklog);

	            return new ResponseEntity<>(worklogDTO, HttpStatus.CREATED);
	        } else {
	            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Task not found");
	        }
	    } catch (IllegalArgumentException e) {
	        return new ResponseEntity<>("Validation failed: " + e.getMessage(), HttpStatus.BAD_REQUEST);
	    } catch (ResponseStatusException e) {
	        return new ResponseEntity<>(e.getReason(), e.getStatusCode());
	    } catch (Exception e) {
	        return new ResponseEntity<>("An unexpected error occurred", HttpStatus.INTERNAL_SERVER_ERROR);
	    }
	}


	@Transactional
	@Override
	public ResponseEntity<?> updateWorklog(Long id, Worklog updatedWorklog) {
	    try {
	        Optional<Worklog> existingWorklogOptional = worklogRepository.findById(id);

	        if (existingWorklogOptional.isPresent()) {
	            Worklog existingWorklog = existingWorklogOptional.get();

	            if (updatedWorklog.getLoggedTimeString() != null) {
	                existingWorklog.setLoggedTimeString(updatedWorklog.getLoggedTimeString());
	                setLoggedTimeFromShorthand(existingWorklog, updatedWorklog.getLoggedTimeString());
	            } else {
	                validateAndAdjustWorklog(existingWorklog);
	            }

	            if (updatedWorklog.getStartDate() != null) {
	                existingWorklog.setStartDate(updatedWorklog.getStartDate());
	            }
	            if (updatedWorklog.getLogDescription() != null) {
	                existingWorklog.setLogDescription(updatedWorklog.getLogDescription());
	            }

	            Task task = existingWorklog.getTask();
	            if (task != null) {
	                Worklog updated = worklogRepository.save(existingWorklog);

	                WorklogDTO updatedWorklogDTO = mapToDTO(updated);

	                return new ResponseEntity<>(updatedWorklogDTO, HttpStatus.OK);
	            } else {
	                return new ResponseEntity<>("Task not found for this Worklog", HttpStatus.NOT_FOUND);
	            }
	        } else {
	            return new ResponseEntity<>("Worklog not found with ID: " + id, HttpStatus.NOT_FOUND);
	        }
	    } catch (IllegalArgumentException e) {
	        return new ResponseEntity<>("Validation failed: " + e.getMessage(), HttpStatus.BAD_REQUEST);
	    } catch (Exception e) {
	        return new ResponseEntity<>("An unexpected error occurred", HttpStatus.INTERNAL_SERVER_ERROR);
	    }
	}


	public void setLoggedTimeFromShorthand(Worklog worklog, String shorthandNotation) {
		if (shorthandNotation == null || shorthandNotation.isEmpty()) {
			throw new IllegalArgumentException("Shorthand notation is missing or empty.");
		}

		char unit = shorthandNotation.charAt(shorthandNotation.length() - 1);
		double value = Double.parseDouble(shorthandNotation.substring(0, shorthandNotation.length() - 1));

		switch (unit) {
		case 'h':
			worklog.setLoggedTime(value / 24);
			break;
		case 'w':
			worklog.setLoggedTime(value * 7);
			break;
		case 'd':
			worklog.setLoggedTime(value);
			break;
		case 'm':
			worklog.setLoggedTime(value * (365.0 / 12));
			break;
		case 'y':
			worklog.setLoggedTime(value * 365);
			break;
		case 'n':
			worklog.setLoggedTime(value / (24 * 60));
			break;
		default:
			throw new IllegalArgumentException("Unsupported time unit in shorthand notation: '" + unit
					+ "'. Supported units are 'd' (days), 'w' (weeks), 'h' (hours), 'm' (minutes), 'y' (years), and 'n' (minutes).");
		}
	}

	public void validateAndAdjustWorklog(Worklog worklog) {
		setLoggedTimeFromShorthand(worklog, worklog.getLoggedTimeString());
		boolean isWithinCurrentTimeFrame = isWithinCurrentTimeFrame(worklog);
		worklog.setFlag(isWithinCurrentTimeFrame);
	}

	public boolean isWithinCurrentTimeFrame(Worklog worklog) {
		LocalDateTime currentTime = LocalDateTime.now();
		LocalDateTime startDate = worklog.getStartDate();
		double loggedTime = worklog.getLoggedTime();

		LocalDateTime endDate = startDate.plusDays((long) loggedTime);
		long secondsToAdd = (long) ((loggedTime % 1) * 24 * 60 * 60);
		endDate = endDate.plusSeconds(secondsToAdd);

		currentTime = currentTime.truncatedTo(ChronoUnit.SECONDS);
		startDate = startDate.truncatedTo(ChronoUnit.SECONDS);
		endDate = endDate.truncatedTo(ChronoUnit.SECONDS);

		boolean isWithinTimeFrame = !currentTime.isBefore(startDate) && !currentTime.isAfter(endDate);

		worklog.setFlag(isWithinTimeFrame);

		return isWithinTimeFrame;
	}

}