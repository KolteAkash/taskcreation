package com.example.Security.controller;

import com.example.Security.auth.ProjectNameUserName;
import com.example.Security.model.Project;
import com.example.Security.model.User;
import com.example.Security.service.ProjectService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/projects")
public class ProjectController {
    @Autowired
    private ProjectService projectService;

    @GetMapping
    public ResponseEntity<List<Project>> getAllProjects(HttpServletRequest request) {

        return projectService.getAllProjects(request);
    }
    @GetMapping("/passing-list")
    public ResponseEntity<?> getAllProjectsAndUsers(HttpServletRequest request) {
        return projectService.getAllProjectsAndUsers(request);
    }

    @GetMapping("/{projectId}")
    public ResponseEntity<Project> getProjectById(@PathVariable int projectId) {
        return projectService.getProjectById(projectId);
    }
//    @PostMapping("/relation-user-project")
//    public ResponseEntity<?> createRelation(HttpServletRequest request, @RequestBody ProjectNameUserName projectNameUserName) {
//        return projectService.createRelation(request,projectNameUserName);
//    }
    
    @GetMapping("/client/users")
    public ResponseEntity<List<User>> getClientUsers(HttpServletRequest request) {
        return projectService.getClientUsers(request);
    }
    
    
    @PostMapping("/relation-user-project")
    public ResponseEntity<?> createRelation(HttpServletRequest request, @RequestBody List<ProjectNameUserName> projectNameUserNames) {
        return projectService.createRelations(request, projectNameUserNames);
    }


    @PostMapping("/create-project")
    public ResponseEntity<Project> createProject(HttpServletRequest request, @RequestBody Project project) {
        return projectService.createProject(request,project);
    }

    @PutMapping("/{projectId}")
    public ResponseEntity<Project> updateProject(HttpServletRequest request,@PathVariable int projectId, @RequestBody Project project) {
        return projectService.updateProject(request,projectId, project);
    }

    @DeleteMapping("/delete-project/{projectId}")
    public ResponseEntity<?> deleteProject(HttpServletRequest request,@PathVariable int projectId) {
            projectService.deleteProject(request,projectId);
            return new ResponseEntity<>("delete project successfully", HttpStatus.NO_CONTENT);


    }
}
