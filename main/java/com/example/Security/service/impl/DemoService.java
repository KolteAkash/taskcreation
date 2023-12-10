package com.example.Security.service.impl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DemoService {

    public ResponseEntity<?> demoService(){

        return ResponseEntity.ok().body("Response from backend");
    }
}
