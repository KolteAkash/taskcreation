package com.example.Security.controller;

import com.example.Security.model.Initiative;
import com.example.Security.model.ProjectType;
import com.example.Security.model.request.InitiativeRequest;
import com.example.Security.repository.InitiativeRepository;
import com.example.Security.repository.ProjectTypeRepository;
import com.example.Security.service.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/project_type")
public class InitiativeController {

    @Autowired
    private InitiativeRepository initiativeRepository;

    @Autowired
    private ProjectTypeRepository projectTypeRepository;

    @GetMapping("/list-project-type")
    public List<ProjectType> getAllPriority(){
        return projectTypeRepository.findAll();

    }
    @PostMapping("/add-type")
    public ProjectType addProjectType(@RequestBody ProjectType projectType){
        return  projectTypeRepository.save(projectType);
    }
    @PostMapping("/add-initiative")
    public Initiative addInitiative(@RequestBody InitiativeRequest initiativeRequest){
        ProjectType projectType=projectTypeRepository.findById(initiativeRequest.getProjectTypeId()).orElseThrow();
        Initiative initiative=new Initiative();
        initiative.setInitiative_name(initiativeRequest.getInitiative_name());
        initiative.setInitiative_description(initiativeRequest.getInitiative_description());
        initiative.setProjectType(projectType);
        return  initiativeRepository.save(initiative);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProjectType(@PathVariable int id) {
        Optional<Initiative> projectType = initiativeRepository.findById(id);
        if (projectType.isEmpty()) {
            throw new ResourceNotFoundException("project type not found with ID " + id);
        }

        initiativeRepository.delete(projectType.get());
        return ResponseEntity.noContent().build();
    }

}
