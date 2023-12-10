package com.example.Security.controller;

import com.example.Security.model.Priority;
import com.example.Security.repository.PriorityRepository;
import com.example.Security.service.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/priority")
public class PriorityController {
    @Autowired
    private PriorityRepository priorityRepository;
    @GetMapping("/list-priority")
    public List<Priority> getAllPriority(){
        return priorityRepository.findAll();

    }
    @PostMapping("/add-priority")
    public Priority addPriority(@RequestBody Priority priority){
        return  priorityRepository.save(priority);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePriority(@PathVariable int id) {
        Optional<Priority> priority = priorityRepository.findById(id);
        if (priority.isEmpty()) {
            throw new ResourceNotFoundException("Priority not found with ID " + id);
        }

        priorityRepository.delete(priority.get());
        return ResponseEntity.noContent().build();
    }


}
