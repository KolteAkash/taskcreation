package com.example.Security.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.Security.auth.AuthenticationResponse;
import com.example.Security.auth.RefreshTokenRequest;
import com.example.Security.model.Role;
import com.example.Security.model.User;
import com.example.Security.repository.RoleCustomRepo;
import com.example.Security.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class JwrService {
    private static final String Secret_key="123";
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleCustomRepo roleCustomRepo;

    public String generateToken(User user, Collection<SimpleGrantedAuthority> authorities){
        Algorithm algorithm = Algorithm.HMAC256(Secret_key.getBytes());
        return JWT.create()
                .withSubject(user.getEmail())
                .withExpiresAt(new Date(System.currentTimeMillis()+ 24 * 60 * 60 * 1000))
                .withClaim("roles",authorities.stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()))
                .sign(algorithm);
    }
    public String generateRefreshToken(User user, Collection<SimpleGrantedAuthority> authorities){
        Algorithm algorithm = Algorithm.HMAC256(Secret_key.getBytes());
        return JWT.create()
                .withSubject(user.getEmail())
                .withExpiresAt(new Date(System.currentTimeMillis()+ 7 * 24 * 60 * 60 * 1000))
                .sign(algorithm);
    }

    public ResponseEntity<?> refreshTokenRequest(RefreshTokenRequest refreshTokenRequest){
        try {
            Algorithm algorithm = Algorithm.HMAC256(Secret_key.getBytes());
            JWTVerifier verifier = JWT.require(algorithm).build();
            DecodedJWT decodedJWT = verifier.verify(refreshTokenRequest.getRefreshToken());
            String username = decodedJWT.getSubject();
            User user = userRepository.findByEmail(username).orElseThrow(()-> new NoSuchElementException("InValid User"));
            List<Role> role=null;
            if(user!=null){
                role=roleCustomRepo.getRole(user);
            }
            Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
            Set<Role> set = new HashSet<>();
            role.stream().forEach(c->set.add(new Role(c.getName())));
            user.setRoles(set);
            set.stream().forEach(i->authorities.add(new SimpleGrantedAuthority(i.getName())));
            var jwtToken =generateToken(user,authorities);
            var jwtRefreshToken =generateRefreshToken(user,authorities);
            return ResponseEntity.ok(AuthenticationResponse.builder().token(jwtToken).refreshToken(jwtRefreshToken).email(user.getEmail()).userName(user.getUser_name()).build());
        }catch (NoSuchElementException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error "+e.getMessage());
        }
        catch (TokenExpiredException e){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Error "+e.getMessage());
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }


}
