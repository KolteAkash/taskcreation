package com.example.Security.service;
import com.example.Security.auth.ProjectNameUserName;
import com.example.Security.model.Project;
import com.example.Security.model.User;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface ProjectService {
	
	ResponseEntity<List<User>> getClientUsers(HttpServletRequest request);
    ResponseEntity<List<Project>> getAllProjects(HttpServletRequest request);
    ResponseEntity<?> getAllProjectsAndUsers(HttpServletRequest request);
    ResponseEntity<Project> createProject(HttpServletRequest request, Project project);

//    ResponseEntity<?> createRelation(HttpServletRequest request, ProjectNameUserName projectNameUserName);
    ResponseEntity<?> createRelations(HttpServletRequest request, List<ProjectNameUserName> projectNameUserNames) ;
    ResponseEntity<Project> getProjectById(int projectId);
    ResponseEntity<Project> updateProject(HttpServletRequest request,int projectId, Project project);
    ResponseEntity<?> deleteProject(HttpServletRequest request,int projectId);
}
