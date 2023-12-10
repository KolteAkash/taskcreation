package com.example.Security.service.impl;

import com.example.Security.config.ErrorResponse;
import com.example.Security.model.*;
import com.example.Security.model.request.StatusRequest;
import com.example.Security.model.request.TaskRequest;
import com.example.Security.model.response.ProjectStatusResponse;
import com.example.Security.model.response.StatusResponse;
import com.example.Security.repository.*;
import com.example.Security.service.TaskService;
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
import java.util.*;

import static org.springframework.http.HttpStatus.*;


@Service
public class TaskImpl implements TaskService {
    @Autowired
    private ProjectRepository projectRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TaskRepository taskRepository;
    @Autowired
    private ProjectStatusRepository projectStatusRepository;

    @Autowired
    private StatusRepository statusRepository;

    @Autowired
    private AuthorizationHelper authorizationHelper;
    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TaskAttachmentRepository taskAttachmentRepository;

    @Autowired
    private PriorityRepository priorityRepository;

    @Value("${file.upload-path}")
    private String uploadPath;

    private Logger logger= LoggerFactory.getLogger(TaskService.class);

    @Override
    public ResponseEntity<?> createTask(HttpServletRequest request, MultipartFile file, String requiredData) {
        String authorizationHeader = request.getHeader("Authorization");
        User user = authorizationHelper.getUserFromToken(authorizationHeader);
        this.logger.info(user.getUser_id());
        this.logger.info(requiredData);
        try {
            // Converting String to Json
            TaskRequest taskRequest = null;
            try {
                taskRequest = objectMapper.readValue(requiredData, TaskRequest.class);
                this.logger.error("task Request$");
                this.logger.info("task request ${} : ",taskRequest);
            } catch (JsonProcessingException e) {
                this.logger.error("Something Went wrong");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Something Went Wrong");
            }

            Project project = projectRepository.findById(taskRequest.getProject_id()).orElseThrow();
            Status status=statusRepository.findById(taskRequest.getStatus_id()).orElseThrow();
            ProjectStatus projectStatus1=projectStatusRepository.findByStatus(status);
            Priority priority=null;
            if(taskRequest.getPriority_id()==0){
                priority=priorityRepository.findById(3).orElseThrow();
            }else{
                priority=priorityRepository.findById(taskRequest.getPriority_id()).orElseThrow();
            }

            this.logger.info("Project Status ${} : ",projectStatus1);
            if(projectStatus1.getProject().getProjectId()!=taskRequest.getProject_id()){
                this.logger.error("Project Id Not match" );
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }
            this.logger.info("Above if "+user.getClientId() );
            if (user.getClientId() == null||project.getProjectId()!=taskRequest.getProject_id()) {
                this.logger.error("Error in second if");
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }
            Task task = new Task();
            task.setCreated_By(user.getEmail());
            task.setTask_details(taskRequest.getTask_details());
            task.setTask_summary(taskRequest.getTask_summary());
            task.setAssignee(taskRequest.getAssignee());
            task.setClientId(user.getClientId());
            task.setProject(project);
            task.setStart_time(new Date());
            task.setEnd_time(new Date(System.currentTimeMillis()+50*60*1000));
            task.setReporter(user.getEmail());
            task.setProjectStatus(projectStatus1);
            task.setPriority(priority);
            Task savedTask=taskRepository.save(task);


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
                    isFileUploaded = true;
                    abc=targetpath;
                    TaskAttachment taskAttachment=new TaskAttachment();
                    taskAttachment.setAttachmentPath(targetpath);
                    taskAttachment.setTask(savedTask);
                    taskAttachment.setCreatedBy(user.getEmail());
                    TaskAttachment savedTaskAttachment=taskAttachmentRepository.save(taskAttachment);
                }catch (IOException e){
                    this.logger.error("Some thing Went Wrong");
                    throw new RuntimeException(e);

                }

            }
            return new ResponseEntity<>(savedTask, HttpStatus.CREATED);
        } catch (Exception e) {
            this.logger.error("Some thing Went Wrong final Catch");
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public ResponseEntity<?> createStatus(HttpServletRequest request, StatusRequest statusRequest) {
        String authorizationHeader = request.getHeader("Authorization");
        User user = authorizationHelper.getUserFromToken(authorizationHeader);
        Project project=projectRepository.findById(statusRequest.getProject_id()).orElseThrow();

        if (user.getClientId() == null||project==null) {
            this.logger.error("Create Status Something went wrong project or user value is null");
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        try {
            Status status=new Status();
            status.setStatus(statusRequest.getStatus());
            statusRepository.save(status);
            ProjectStatus projectStatus=new ProjectStatus();
            projectStatus.setStatus(status);
            projectStatus.setProject(project);
            projectStatus.setClientId(user.getClientId());
            projectStatusRepository.save(projectStatus);
            return new ResponseEntity<>(projectStatus, HttpStatus.CREATED);
        } catch (Exception e) {
            // Handle any other exceptions
            this.logger.error("Create status exception");
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @Override
    public ResponseEntity<?> getAllDataAboutProject(int id, HttpServletRequest request) {
        String authorizationHeader = request.getHeader("Authorization");
        User user = authorizationHelper.getUserFromToken(authorizationHeader);

        List<ProjectStatus> statusList = projectStatusRepository.findByProject_ProjectIdAndClientId(id, user.getClientId());
        if(statusList.isEmpty()){
            return new ResponseEntity<>("Empty",HttpStatus.NO_CONTENT);
        }
        List<Task> tasks = taskRepository.findByProject_ProjectId(id);

        ProjectStatusResponse response = new ProjectStatusResponse();

        // Populate project details
        Project project = statusList.get(0).getProject();
        response.setProject(project);
        response.setProject_status_id(statusList.get(0).getProject_status_id());
        response.setClientId(statusList.get(0).getClientId());
        List<StatusResponse> statusResponseList = new ArrayList<>();
        // Process each project status
        for (ProjectStatus projectStatus : statusList) {

            StatusResponse statusResponse = new StatusResponse();
            List<Task> tasksForStatus = new ArrayList<>();

            for (Task task : tasks) {
                if (task.getProjectStatus().getStatus().getStatus_id() == projectStatus.getStatus().getStatus_id()) {
                    tasksForStatus.add(task);
                }
            }
            tasksForStatus.sort(Comparator.comparingInt(t -> t.getPriority().getPriority_id()));
            statusResponse.setStatus_id(projectStatus.getStatus().getStatus_id());
            statusResponse.setStatus(projectStatus.getStatus());
            statusResponse.setTasks(tasksForStatus);

            if(!statusResponseList.contains(statusResponse)) {
                statusResponseList.add(statusResponse);
            }

            response.setStatusResponses(statusResponseList);
        }

        this.logger.info("project status response ${} ",response.getStatusResponses());

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    @Override
    public ResponseEntity<?> updateTaskStatus(HttpServletRequest request,String taskId, TaskRequest taskRequest) {
        String authorizationHeader = request.getHeader("Authorization");
        User user = authorizationHelper.getUserFromToken(authorizationHeader);
        Task existingTask = taskRepository.findById(taskId).orElse(null);
        Status status =statusRepository.findById(taskRequest.getStatus_id()).orElseThrow();
        ProjectStatus projectStatus=projectStatusRepository.findByStatus(status);
        if (existingTask == null) {
            this.logger.warn("task not exists");
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } else {
            existingTask.setProjectStatus(projectStatus);
            existingTask.setUpdated_By(user.getEmail());
            Task updatedTask = taskRepository.save(existingTask);
            return new ResponseEntity<>(taskRequest.getStatus_id(), HttpStatus.OK);
        }
    }

    @Override
    public ResponseEntity<?> allProjectStatus(int id, HttpServletRequest request) {
        String authorizationHeader = request.getHeader("Authorization");
        User user = authorizationHelper.getUserFromToken(authorizationHeader);
        this.logger.info(user.getEmail());
        List<ProjectStatus> statusList = projectStatusRepository.findByProject_ProjectIdAndClientId(id,user.getClientId());
        List<Status> statuses=new ArrayList<>();
        for (ProjectStatus projectStatus : statusList){
            statuses.add(projectStatus.getStatus());
        }
            if(statusList.isEmpty()){
            return new ResponseEntity<>("Empty",HttpStatus.NO_CONTENT);
        }

        return new ResponseEntity<>(statuses, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<?> updateTask(HttpServletRequest request,String taskId,MultipartFile file, String requiredData) {
        String authorizationHeader = request.getHeader("Authorization");
        User user = authorizationHelper.getUserFromToken(authorizationHeader);
        this.logger.warn(taskId);
        this.logger.info(requiredData);
        try {
            // Converting String to Json
            TaskRequest taskRequest = null;
            try {
                taskRequest = objectMapper.readValue(requiredData, TaskRequest.class);
                this.logger.info("task Request$");
                this.logger.info("task request ${} : ",taskRequest);
            } catch (JsonProcessingException e) {
                this.logger.error("Something Went wrong");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Something Went Wrong");
            }

            Project project = projectRepository.findById(taskRequest.getProject_id()).orElseThrow();
            Status status=statusRepository.findById(taskRequest.getStatus_id()).orElseThrow();
            ProjectStatus projectStatus1=projectStatusRepository.findByStatus(status);
            Priority priority=null;
            if(taskRequest.getPriority_id()==0){
                priority=priorityRepository.findById(3).orElseThrow();
            }else{
                priority=priorityRepository.findById(taskRequest.getPriority_id()).orElseThrow();
            }
            if(projectStatus1.getProject().getProjectId()!=taskRequest.getProject_id()){
                this.logger.error("Project Id Not match" );
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }
            this.logger.info("Above if "+user.getClientId() );
            if (user.getClientId() == null||project.getProjectId()!=taskRequest.getProject_id()) {
                this.logger.error("Error in second update Task");
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }
            Task task = taskRepository.findById(taskId).orElseThrow();
            task.setTask_details(taskRequest.getTask_details());
            task.setTask_summary(taskRequest.getTask_summary());
            if(taskRequest.getAssignee()==null){
                if(task.getAssignee()==null){
                    task.setAssignee("");
                }else{
                    task.setAssignee(task.getAssignee());
                }

            }else{
                task.setAssignee(taskRequest.getAssignee());
            }
            task.setStart_time(new Date());
            task.setUpdated_By(user.getEmail());
            task.setEnd_time(new Date(System.currentTimeMillis()+50*60*1000));
            task.setProjectStatus(projectStatus1);
            task.setPriority(priority);
            this.logger.warn("before saved task: ${}: ",task);
            Task savedTask=taskRepository.save(task);


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
                    isFileUploaded = true;
                    abc=targetpath;
                    TaskAttachment taskAttachment=new TaskAttachment();
                    taskAttachment.setAttachmentPath(targetpath);
                    taskAttachment.setTask(savedTask);
                    taskAttachment.setCreatedBy(user.getEmail());
                    TaskAttachment savedTaskAttachment=taskAttachmentRepository.save(taskAttachment);
                }catch (IOException e){
                    this.logger.error("Some thing Went Wrong");
                    throw new RuntimeException(e);

                }

            }
            return new ResponseEntity<>(savedTask, HttpStatus.CREATED);
        } catch (Exception e) {
            this.logger.error("Some thing Went Wrong final Catch");
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @Override
    public ResponseEntity<?> deleteTask(HttpServletRequest request,String taskId) {
        String authorizationHeader = request.getHeader("Authorization");
        User user = authorizationHelper.getUserFromToken(authorizationHeader);
        try{
            Task task=taskRepository.findById(taskId).orElseThrow(()->new NoSuchElementException("Task Not Found"));
            if(task.getClientId().equals(user.getClientId())){
                TaskAttachment taskAttachment=taskAttachmentRepository.findByTask(task);
                if (taskAttachment != null) {
                    taskAttachmentRepository.delete(taskAttachment);
                }
                taskRepository.delete(task);
                ErrorResponse errorResponse = new ErrorResponse(OK, "Task Deleted");
                return ResponseEntity.ok(errorResponse);
            }else{
                this.logger.error("Client Id not match");
                ErrorResponse errorResponse = new ErrorResponse(UNAUTHORIZED, "You  don't have permission");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
            }
        }catch(NoSuchElementException e) {
            this.logger.error("Task Not Found");
            ErrorResponse errorResponse = new ErrorResponse(NOT_FOUND, "Task Not Found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }
        catch (Exception e) {
            this.logger.error("Something went Wrong In Delete project");
            ErrorResponse errorResponse = new ErrorResponse(INTERNAL_SERVER_ERROR, "Something went Wrong In Delete project");
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

}
