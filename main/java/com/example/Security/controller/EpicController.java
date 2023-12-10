package com.example.Security.controller;

import com.example.Security.model.Initiative;
import com.example.Security.service.EpicService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/epic")
public class EpicController {
    @Autowired
    private EpicService epicService;

    @PostMapping("/create-epic")
    public ResponseEntity<?> createEpic(HttpServletRequest request, @RequestParam(value = "file",required = false) MultipartFile file, @RequestParam("requiredData") String requiredData) {
        return epicService.createEpic(request,file,requiredData);
    }
}
