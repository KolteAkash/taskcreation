package com.example.Security.service;

import com.example.Security.auth.RegisterRequest;
import com.example.Security.model.Client;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface ClientService {
    ResponseEntity<List<Client>> getAllClients();
    ResponseEntity<Client> createClient(HttpServletRequest request, Client client);
    ResponseEntity<?> createUser(HttpServletRequest request, RegisterRequest registerRequest);


}
