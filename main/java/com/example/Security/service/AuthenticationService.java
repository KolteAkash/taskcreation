package com.example.Security.service;

import com.example.Security.auth.AuthenticationRequest;
import com.example.Security.auth.AuthenticationResponse;
import com.example.Security.auth.RegisterRequest;
import com.example.Security.config.ErrorResponse;
import com.example.Security.model.Role;
import com.example.Security.model.User;
import com.example.Security.repository.RoleCustomRepo;
import com.example.Security.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.*;

import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final RoleCustomRepo roleCustomRepo;
    private final JwrService jwrService;
    private final UserService userService;
    private Logger logger= LoggerFactory.getLogger(AuthenticationService.class);
    public ResponseEntity<?> register(RegisterRequest request){
        try {
            if(userRepository.existsById(request.getEmail().toString())){
                this.logger.error("User Not Found ${} :",request.getEmail());
                throw new IllegalArgumentException("User with email " + request.getEmail() + " already exists");
            }

            userService.saveUser(new User(request.getMobile_number().toString(), request.getUser_name().toString(), request.getEmail(), request.getPassword().toString(), new HashSet<>(),"default", "default"));
            userService.addToUser(request.getEmail().toString(), "ROLE_ADMIN");
            User user = userRepository.findByEmail(request.getEmail()).orElseThrow();

            return ResponseEntity.ok(user);
        }catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

        public ResponseEntity<?> authenticate(AuthenticationRequest authenticationRequest){
        try {
            User user=userRepository.findByEmail(authenticationRequest.getEmail()).orElseThrow(()->new NoSuchElementException("User Not Found"));
            this.logger.info("User Info ${}",user.getEmail());
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authenticationRequest.getEmail(),authenticationRequest.getPassword()));

            List<Role> role=null;
            if(user!=null){
                role=roleCustomRepo.getRole(user);
            }
            Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
            Set<Role> set = new HashSet<>();
            role.stream().forEach(c->{set.add(new Role(c.getName()));
                authorities.add(new SimpleGrantedAuthority(c.getName()));
            });
            user.setRoles(set);
            set.stream().forEach(i->authorities.add(new SimpleGrantedAuthority(i.getName())));
            var jwtToken =jwrService.generateToken(user,authorities);
            var jwtRefreshToken =jwrService.generateRefreshToken(user,authorities);
            return ResponseEntity.ok(AuthenticationResponse.builder().token(jwtToken).refreshToken(jwtRefreshToken).userName(user.getUser_name()).email(user.getEmail()).build());
        }catch (NoSuchElementException e){
            this.logger.error("Invalid credentials User Mail not found");
            ErrorResponse errorResponse = new ErrorResponse(NOT_FOUND, "Invalid credentials");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }catch (BadCredentialsException e) {
            this.logger.error("Invalid Bad credentials password wrong");
            ErrorResponse errorResponse = new ErrorResponse(NOT_FOUND, "Invalid credentials");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
        }catch (Exception e){
            this.logger.error("Internal server error");
            ErrorResponse errorResponse = new ErrorResponse(NOT_FOUND, "Internal server error");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }

    }

}
