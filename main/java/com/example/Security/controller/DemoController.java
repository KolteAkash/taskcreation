package com.example.Security.controller;

import com.example.Security.service.AuthenticationService;
import com.example.Security.service.impl.DemoService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/validator")
@RequiredArgsConstructor
@CrossOrigin
public class DemoController {

    private final AuthenticationService authenticationService;
    @Autowired
    private DemoService demoService;
    @GetMapping("/checkList")
    public ResponseEntity<?> login(){
        return ResponseEntity.ok(demoService.demoService());
    }
}
