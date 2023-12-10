package com.example.Security.service;

import com.example.Security.model.*;
import com.example.Security.model.request.EpicRequest;
import com.example.Security.model.request.TaskRequest;
import com.example.Security.repository.EpicRepository;
import com.example.Security.repository.InitiativeRepository;
import com.example.Security.service.token.AuthorizationHelper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Date;
import java.util.UUID;

@Service
public class EpicService {

    private Logger logger= LoggerFactory.getLogger(TaskService.class);
    @Autowired
    private AuthorizationHelper authorizationHelper;

    @Autowired
    private EpicRepository epicRepository;
    @Value("${file.json-upload-path}")
    private String uploadPath;
    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private InitiativeRepository initiativeRepository;

    public ResponseEntity<?> createEpic(HttpServletRequest request, MultipartFile file, String requiredData) {
        String authorizationHeader = request.getHeader("Authorization");
        User user = authorizationHelper.getUserFromToken(authorizationHeader);
        this.logger.info(user.getUser_id());
        this.logger.info(requiredData);
        try {
            EpicRequest epicRequest = null;
            try {
                epicRequest = objectMapper.readValue(requiredData, EpicRequest.class);
                this.logger.info("epic request ${} : ",epicRequest);
            } catch (JsonProcessingException e) {
                this.logger.error("epic Something Went wrong");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("epic Something Went Wrong");
            }



            String abc=null;
            if (file != null && !file.isEmpty()) {//check file is present or not
                var originalFileName = file.getOriginalFilename();//to get original file name
                var fileExtension = originalFileName.substring(originalFileName.lastIndexOf("."));//get extension
                var randomFileName = UUID.randomUUID().toString() + fileExtension;
                var targetpath = uploadPath+"/"+randomFileName;
                this.logger.info("new file name :${} ",randomFileName);
                boolean isFileUploaded = false;

                try {
                    if (!Files.exists(Path.of(uploadPath))) {
                        Files.createDirectories(Path.of(uploadPath));
                    }
                    Files.copy(file.getInputStream(),Path.of(targetpath));
                    Initiative initiative=initiativeRepository.findById(epicRequest.getInitiativeId()).orElseThrow();
                    isFileUploaded = true;
                    abc=targetpath;
                    Epic epic=new Epic();
                    epic.setJsonFilePath(targetpath);
                    epic.setEpicDescription(epicRequest.getEpicDescription());
                    epic.setEpicName(epicRequest.getEpicName());
                    epic.setInitiative(initiative);
                    epicRepository.save(epic);
                    return new ResponseEntity<>(epic, HttpStatus.CREATED);
                }catch (IOException e){
                    this.logger.error("Some thing Went Wrong");
                    throw new RuntimeException(e);

                }

            }else {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }

        } catch (Exception e) {
            this.logger.error("Some thing Went Wrong final Catch");
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
