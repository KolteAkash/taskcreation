package com.example.Security.service.impl;

import com.example.Security.auth.AllUserListResponse;
import com.example.Security.model.*;
import com.example.Security.repository.*;
import com.example.Security.service.TaskService;
import com.example.Security.service.UserService;
import com.example.Security.service.token.AuthorizationHelper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private AuthorizationHelper authorizationHelper;

    @Autowired
    private ClientUserRepository clientUserRepository;

    @Autowired
    private ProjectUserRepository projectUserRepository;

    private final PasswordEncoder passwordEncoder;


    private Logger logger= LoggerFactory.getLogger(UserService.class);

    @Override
    public User saveUser(User user) {
       user.setPassword(passwordEncoder.encode(user.getPassword()));
       user.setRoles(new HashSet<>());
       return userRepository.save(user);
    }

    @Override
    public Role saveRole(Role role) {
        return roleRepository.save(role);
    }

    @Override
    public void addToUser(String username, String rolename) {
        if (!userRepository.findByEmail(username).isPresent()) {
            throw new IllegalArgumentException("User with email " + username + " does not exist");
        }
        Role role = roleRepository.findByName(rolename);
        if (role == null) {
            throw new IllegalArgumentException("Role with name " + rolename + " does not exist");
        }

        User user = userRepository.findByEmail(username).get();
        user.getRoles().add(role);
    }
    @Override
    public ResponseEntity<List<AllUserListResponse>> getAllUser(int projectId,HttpServletRequest request){
            String authorizationHeader = request.getHeader("Authorization");
            User user = authorizationHelper.getUserFromToken(authorizationHeader);
            List<User> projectUser= projectUserRepository.findUsersByProjectIdAndClientId(projectId,user.getClientId());
            this.logger.info("all project List ${} ",projectUser);
        List<AllUserListResponse> allUserListResponse = projectUser.stream()
                .map(u -> new AllUserListResponse(u.getUser_name(), u.getEmail()))
                .collect(Collectors.toList());
            return  ResponseEntity.ok(allUserListResponse);
    }
    
    @Override
    public ResponseEntity<List<AllUserListResponse>> getUsersByClient(HttpServletRequest request) {
        String authorizationHeader = request.getHeader("Authorization");
        User user = authorizationHelper.getUserFromToken(authorizationHeader);

        List<User> users = userRepository.findUsersByClientId(user.getClientId());
        this.logger.info("Users for client {}: {}", user.getClientId(), users);

        List<AllUserListResponse> allUserListResponse = users.stream()
                .map(u -> new AllUserListResponse(u.getUser_name(), u.getEmail()))
                .collect(Collectors.toList());

        return ResponseEntity.ok(allUserListResponse);
    }

}
