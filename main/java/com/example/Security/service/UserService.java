package com.example.Security.service;

import com.example.Security.auth.AllUserListResponse;
import com.example.Security.model.Client;
import com.example.Security.model.Project;
import com.example.Security.model.Role;
import com.example.Security.model.User;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface UserService {
    User saveUser(User user);
    Role saveRole(Role role);
    void addToUser(String username, String rolename);

    ResponseEntity<List<AllUserListResponse>> getUsersByClient(HttpServletRequest request);
    ResponseEntity<List<AllUserListResponse>> getAllUser(int projectId,HttpServletRequest request);

}
