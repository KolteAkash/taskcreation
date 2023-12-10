package com.example.Security.service.token;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.Security.model.User;
import com.example.Security.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import com.auth0.jwt.JWTVerifier;

@Component
public class AuthorizationHelper {
    private final UserRepository userRepository;
    private final String secretKey;

    @Autowired
    public AuthorizationHelper(UserRepository userRepository, @Value("${secret.key}") String secretKey) {
        this.userRepository = userRepository;
        this.secretKey = secretKey;
    }
    public User getUserFromToken(String authorizationHeader) {
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String token = authorizationHeader.substring("Bearer ".length());
            Algorithm algorithm = Algorithm.HMAC256(secretKey.getBytes());
            JWTVerifier verifier = JWT.require(algorithm).build();
            DecodedJWT decodedJWT = verifier.verify(token);
            return userRepository.findByEmail(decodedJWT.getSubject()).orElseThrow();
        } else {
            throw new IllegalArgumentException("Invalid authorization header");
        }
    }
}
