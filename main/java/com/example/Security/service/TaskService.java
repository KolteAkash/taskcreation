package com.example.Security.service;

import com.example.Security.model.Task;
import com.example.Security.model.request.StatusRequest;
import com.example.Security.model.request.TaskRequest;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.multipart.MultipartFile;

public interface TaskService {
    ResponseEntity<?> getAllDataAboutProject(int id,HttpServletRequest request);
    ResponseEntity<?> allProjectStatus(int id,HttpServletRequest request);
    ResponseEntity<?> createTask(HttpServletRequest request, MultipartFile file, String requiredData);
    ResponseEntity<?> updateTask(HttpServletRequest request,String taskId,MultipartFile file, String requiredData);
    ResponseEntity<?> deleteTask(HttpServletRequest request,String taskId);

    ResponseEntity<?> createStatus(HttpServletRequest request, StatusRequest statusRequest);

    ResponseEntity<?> updateTaskStatus(HttpServletRequest request, @PathVariable String taskId, TaskRequest taskRequest);
}
