package com.example.Security.service.impl;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.Security.auth.RegisterRequest;
import com.example.Security.model.Client;
import com.example.Security.model.Client_user;
import com.example.Security.model.SuperAdmin_client;
import com.example.Security.model.User;
import com.example.Security.repository.ClientRepository;
import com.example.Security.repository.ClientUserRepository;
import com.example.Security.repository.SuperAdminClientRepository;
import com.example.Security.repository.UserRepository;
import com.example.Security.service.ClientService;
import com.example.Security.service.UserService;
import com.example.Security.service.token.AuthorizationHelper;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;

@Service
public class ClientImpl implements ClientService {
    @Autowired
    private ClientRepository clientRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private ClientUserRepository clientUserRepository;
    @Autowired
    private SuperAdminClientRepository superAdminClientRepository;

    private Logger logger= LoggerFactory.getLogger(ClientService.class);
    @Autowired
    private AuthorizationHelper authorizationHelper;
    @Override
    public ResponseEntity<List<Client>> getAllClients() {
        List<Client> clients = clientRepository.findAll();
        if (clients.isEmpty()) {
            this.logger.info("Client List is Empty");
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            this.logger.info("Client List ${}",clients);
            return new ResponseEntity<>(clients, HttpStatus.OK);
        }
    }

    @Override
    public ResponseEntity<Client> createClient(HttpServletRequest request, Client client) {
            String authorizationHeader = request.getHeader("Authorization");
            User user = authorizationHelper.getUserFromToken(authorizationHeader);
            this.logger.info("created by ${}",user.getEmail());//super admin
            User clientAdmin=userRepository.findByEmail(client.getClient_admin()).orElseThrow(()->new UsernameNotFoundException("User Not Found"));//clientAdmin
            this.logger.info("client mail ${}",clientAdmin);
            Client saveClient = clientRepository.save(client);
            clientAdmin.setClientId(client.getClient_id());
            userRepository.save(clientAdmin);
            SuperAdmin_client superAdminClient = new SuperAdmin_client();
            superAdminClient.setClient_id(client);
            superAdminClient.setUser_id(user);
            superAdminClientRepository.save(superAdminClient);
            return new ResponseEntity<>(saveClient, HttpStatus.CREATED);

    }
    @Override
    public ResponseEntity<?> createUser(HttpServletRequest request, RegisterRequest registerRequest){
        try {
                String authorizationHeader = request.getHeader("Authorization");
                User loggedUser = authorizationHelper.getUserFromToken(authorizationHeader);
                User admin=userRepository.findByEmail(loggedUser.getEmail()).orElseThrow();
                this.logger.info("Client Admin ${}",admin);
                if(!userRepository.existsById(registerRequest.getEmail())) {
                    userService.saveUser(new User(registerRequest.getMobile_number().toString(), registerRequest.getUser_name().toString(), registerRequest.getEmail(), registerRequest.getPassword().toString(), new HashSet<>(), loggedUser.getEmail(), "default"));
                    userService.addToUser(registerRequest.getEmail().toString(), "ROLE_USER");
                    User user = userRepository.findByEmail(registerRequest.getEmail()).orElseThrow(() -> new IllegalArgumentException("User already defined"));
                    user.setClientId(admin.getClientId());
                    userRepository.save(user);
                    this.logger.info("Added User ${}",user);
                    Client client = clientRepository.findById(admin.getClientId()).orElseThrow();
                    Client_user clientUser = new Client_user();
                    clientUser.setClient(client);
                    clientUser.setUser(user);
                    clientUserRepository.save(clientUser);
                    return ResponseEntity.ok(user);
                }else {
                    this.logger.error("User Already Present");
                    throw new IllegalArgumentException("User Already present");
                }
        }catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
        }
    }
}
