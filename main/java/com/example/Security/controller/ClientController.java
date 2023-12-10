package com.example.Security.controller;

import com.example.Security.auth.RegisterRequest;
import com.example.Security.model.Client;
import com.example.Security.service.ClientService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/client")
public class ClientController {
    @Autowired
    private ClientService clientService;


    @GetMapping
    public ResponseEntity<List<Client>> getAllClients(){
        return clientService.getAllClients();
    }
    @PostMapping("/create-client")
    public ResponseEntity<Client> createClient(HttpServletRequest request, @RequestBody Client client) {
        return clientService.createClient(request,client);
    }


}
