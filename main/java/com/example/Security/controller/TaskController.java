package com.example.Security.controller;

import com.example.Security.model.request.StatusRequest;
import com.example.Security.model.request.TaskRequest;
import com.example.Security.service.TaskService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


@RestController
@RequestMapping("/task")
public class TaskController {
    @Autowired
    private TaskService taskService;

    @GetMapping("/status/{id}")
    public ResponseEntity<?> getAllDataAboutProject(@PathVariable int id, HttpServletRequest request) {
        return taskService.getAllDataAboutProject(id,request);
    }
    @GetMapping("/status/all-project-status/{id}")
    public ResponseEntity<?> allProjectStatus(@PathVariable int id, HttpServletRequest request) {
        return taskService.allProjectStatus(id,request);
    }
    @PostMapping("/create-task")
    public ResponseEntity<?> createTask(HttpServletRequest request, @RequestParam(value = "file",required = false) MultipartFile file, @RequestParam("requiredData") String requiredData) {
        return taskService.createTask(request,file,requiredData);
    }
    @PutMapping("/update-task/{id}")
    public ResponseEntity<?> updateTask(HttpServletRequest request,@PathVariable("id") String taskId,@RequestParam(value = "file",required = false) MultipartFile file, @RequestParam("requiredData") String requiredData) {
        return taskService.updateTask(request,taskId,file,requiredData);
    }
    @PostMapping("/create-status")
    public ResponseEntity<?> createStatus(HttpServletRequest request, @RequestBody StatusRequest statusRequest) {
        return taskService.createStatus(request,statusRequest);
    }

    @PutMapping("/update-status-task/{id}")
    public ResponseEntity<?> updateTaskStatus(HttpServletRequest request, @PathVariable("id") String taskId, @RequestBody TaskRequest taskRequest) {
        return taskService.updateTaskStatus(request,taskId, taskRequest);
    }
    @DeleteMapping("/delete-task/{id}")
    public ResponseEntity<?> deleteTask(HttpServletRequest request, @PathVariable("id") String taskId) {
        return taskService.deleteTask(request,taskId);
    }
}
