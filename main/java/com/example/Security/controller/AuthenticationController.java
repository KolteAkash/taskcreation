package com.example.Security.controller;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.Security.auth.AuthenticationRequest;
import com.example.Security.auth.AuthenticationResponse;
import com.example.Security.auth.RefreshTokenRequest;
import com.example.Security.auth.RegisterRequest;
import com.example.Security.model.Role;
import com.example.Security.model.User;
import com.example.Security.repository.RoleCustomRepo;
import com.example.Security.repository.UserRepository;
import com.example.Security.service.AuthenticationService;
import com.example.Security.service.JwrService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.web.bind.annotation.*;

import java.util.*;


@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {
    private final AuthenticationService authenticationService;

    private final JwrService jwrService;
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request){
        return authenticationService.register(request);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthenticationRequest authenticationRequest){
        return authenticationService.authenticate(authenticationRequest);
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshAccessToken(@RequestBody RefreshTokenRequest refreshTokenRequest) {
        return ResponseEntity.ok(jwrService.refreshTokenRequest(refreshTokenRequest));
    }

}
