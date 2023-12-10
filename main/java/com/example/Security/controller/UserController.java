package com.example.Security.controller;

import com.example.Security.auth.AllUserListResponse;
import com.example.Security.auth.RegisterRequest;
import com.example.Security.model.Client;
import com.example.Security.service.ClientService;
import com.example.Security.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admincontroll")
public class UserController {
    @Autowired
    private ClientService clientService;
    @Autowired
    private UserService userService;

    @GetMapping("/users/{id}")
    public ResponseEntity<List<AllUserListResponse>> getAllUser(@PathVariable("id") int projectId,HttpServletRequest request){
        return userService.getAllUser(projectId,request);
    }

    @PostMapping("/create_user")//as a developer
    public ResponseEntity<?> createUser(HttpServletRequest request, @RequestBody RegisterRequest registerRequest) {
        return clientService.createUser(request,registerRequest);
    }
    
    @GetMapping("/client/users")
    public ResponseEntity<List<AllUserListResponse>> getUsersByClient(HttpServletRequest request) {
        return userService.getUsersByClient(request);
    }
}
