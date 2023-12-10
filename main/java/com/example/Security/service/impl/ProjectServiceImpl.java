package com.example.Security.service.impl;

import com.example.Security.auth.ProjectNameUserName;
import com.example.Security.model.*;
import com.example.Security.repository.*;
import com.example.Security.service.ProjectService;
import com.example.Security.service.token.AuthorizationHelper;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import org.hibernate.Hibernate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ProjectServiceImpl implements ProjectService {
    @Autowired
    private ProjectRepository projectRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ClientRepository clientRepository;
    @Autowired
    private ProjectUserRepository projectUserRepository;
    @Autowired
    private ClientUserRepository clientUserRepository;

    @Autowired
    private AuthorizationHelper authorizationHelper;
    @Autowired
    private StatusRepository statusRepository;

    @Autowired
    private TaskRepository taskRepository;
    @Autowired
    private ProjectStatusRepository projectStatusRepository;

    @Autowired
    private PriorityRepository priorityRepository;

    @Autowired
    private ProjectTypeRepository projectTypeRepository;

    @Autowired
    private InitiativeRepository initiativeRepository;

    @Autowired
    private EpicRepository epicRepository;
    private Logger logger= LoggerFactory.getLogger(ProjectService.class);
    
    
    @Override
    public ResponseEntity<List<User>> getClientUsers(HttpServletRequest request) {
        String authorizationHeader = request.getHeader("Authorization");
        User user = authorizationHelper.getUserFromToken(authorizationHeader);

        // Check if the requesting user has the necessary permissions to retrieve the client's users
        if (user == null || user.getClientId() == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Collections.emptyList());
        }

        List<User> users = projectUserRepository.findUsersByClientId(user.getClientId());

        return ResponseEntity.ok(users);
    }
    



    @Override
    public ResponseEntity<List<Project>> getAllProjects(HttpServletRequest request) {
        String authorizationHeader = request.getHeader("Authorization");
        User user = authorizationHelper.getUserFromToken(authorizationHeader);
//            List<Project> assignedProjects = new ArrayList<>();
//            List<Project_user> allProjectUsers = projectUserRepository.findAll();
//            for (Project_user projectUser : allProjectUsers) {
//                if (projectUser.getClient().getClient_id() == user.getClientId() &&
//                        projectUser.getUser().getUser_id().equals(user.getUser_id()) && projectUser.getProject().getClient_id().equals(user.getClientId())) {
//                    assignedProjects.add(projectUser.getProject());
//                }
//            }
        List<Project> assignedProjects = projectUserRepository.findProjectsByUserIdAndClientId(
                user.getUser_id(),
                user.getClientId()
        );
        this.logger.info("User Project list${}",assignedProjects);
        return new ResponseEntity<>(assignedProjects, HttpStatus.OK);

    }
    @Override
    public ResponseEntity<?> getAllProjectsAndUsers(HttpServletRequest request) {
        String authorizationHeader = request.getHeader("Authorization");
        User user = authorizationHelper.getUserFromToken(authorizationHeader);
            List<User> assignedUser = new ArrayList<>();
            List<Project_user> allProjectUsers = projectUserRepository.findAll();
            this.logger.warn("Only for admin request");
            this.logger.info("Client All Projects",allProjectUsers);
            List<Client_user> allUsers = clientUserRepository.findAll();
            this.logger.info("Client Users ${}",allUsers);
        List<Project> assignedProjects = projectUserRepository.findProjectsByUserIdAndClientId(
                user.getUser_id(),
                user.getClientId()
        );
            for (Client_user clientUser : allUsers) {
                if (clientUser.getUser().getClientId().equals(user.getClientId())) {
                    User detachedUser= (User)Hibernate.unproxy(clientUser.getUser());
                    assignedUser.add(detachedUser);
                }
            }
            Map<String, Object> response = new HashMap<>();
            response.put("projects", assignedProjects);
            response.put("users", assignedUser);

            return ResponseEntity.ok(response);
    }
    @Override
    public ResponseEntity<Project> createProject(HttpServletRequest request, Project project) {
        String authorizationHeader = request.getHeader("Authorization");
        User user = authorizationHelper.getUserFromToken(authorizationHeader);


        if (user == null || user.getClientId() == null) {
            this.logger.error("Create Project User Not Found");
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        try {
            project.setCreatedBy(user.getEmail());
            project.setClient_id(user.getClientId());
            Project savedProject = projectRepository.save(project);

            Optional<Client> optionalClient = clientRepository.findById(user.getClientId());
            if (optionalClient.isEmpty()) {
                this.logger.error("Client is not declared");
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }

            Client client = optionalClient.get();

            Project_user projectUser = new Project_user();
            projectUser.setProject(savedProject);
            projectUser.setClient(client);
            projectUser.setUser(user);
            projectUserRepository.save(projectUser);
                try {

                    ObjectMapper objectMapper = new ObjectMapper();
                    this.logger.info(project.getProjectType());
                    int projectTypeId=projectTypeRepository.findProjectTypeIdByProjectType(project.getProjectType());
                    List<Initiative> initiatives=initiativeRepository.findByProjectTypeId(projectTypeId);

                    List<Epic> epics=epicRepository.findEpicsByInitiatives(initiatives);
                    ArrayList<ProjectStatus> allPresentInitiatives=new ArrayList<>();
                    ProjectStatus savedProjectStatus = null;
                    if (!epics.isEmpty()) {


                        for (Epic epic : epics) {

                            try {
                                boolean isNotPresent = !allPresentInitiatives.stream()
                                        .map(ProjectStatus::getStatus)
                                        .anyMatch(status -> status.equals(epic.getInitiative().getInitiative_name()));

                                if (isNotPresent){
                                    Status statusEntity = new Status();
                                    statusEntity.setStatus(epic.getInitiative().getInitiative_name());
                                    Status savedStatus = statusRepository.save(statusEntity);
                                    ProjectStatus projectStatus = new ProjectStatus();
                                    projectStatus.setStatus(savedStatus);
                                    projectStatus.setProject(savedProject);
                                    projectStatus.setClientId(user.getClientId());
                                    savedProjectStatus = projectStatusRepository.save(projectStatus);
                                    allPresentInitiatives.add(savedProjectStatus);
                                }else{
                                    ProjectStatus existingProjectStatus1 = allPresentInitiatives.stream()
                                            .filter(projectStatus -> projectStatus.getStatus().equals(epic.getInitiative().getInitiative_name()))
                                            .findFirst()
                                            .orElse(null);
                                    ProjectStatus existingProjectStatus=projectStatusRepository.findByStatus(existingProjectStatus1.getStatus());
                                    savedProjectStatus=existingProjectStatus;
                                }
                                File file = new File(epic.getJsonFilePath());
                                JsonNode jsonNode = objectMapper.readTree(file);
                                this.logger.warn("JSON File Contents: " + jsonNode);
                                for (JsonNode node : jsonNode) {
                                    // Get the value of the "Task" field and print it
                                    String task = node.get("Task").asText();
                                    String Description = node.get("Description").asText();
                                    int priority1 = node.get("Priority").asInt();
                                    Priority priority = priorityRepository.findById(priority1).orElseThrow();
                                    Task taskEntity = new Task();
                                    taskEntity.setTask_summary(task);
                                    taskEntity.setProject(savedProject);
                                    taskEntity.setTask_details(Description);
                                    taskEntity.setClientId(user.getClientId());
                                    taskEntity.setPriority(priority);
                                    taskEntity.setProjectStatus(savedProjectStatus);
                                    taskRepository.save(taskEntity);

                                }
                            } catch (IOException e) {
                                this.logger.error("Json File Error");
                            }

                        }

                    }
                }catch (Exception e){
                    this.logger.error("json file error");
                }
            return new ResponseEntity<>(savedProject, HttpStatus.CREATED);
        } catch (Exception e) {
            // Handle any other exceptions
            this.logger.error("create Project Something went wrong");
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public ResponseEntity<Project> getProjectById(int projectId) {

        Project project = projectRepository.findById(projectId).orElse(null);
        this.logger.info("Single Project ${}",project);
        if (project == null) {
            this.logger.error("Project with this id ${} Not Found",projectId);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } else {
            return new ResponseEntity<>(project, HttpStatus.OK);
        }
    }
    @Override
    public ResponseEntity<Project> updateProject(HttpServletRequest request,int projectId, Project project) {
        String authorizationHeader = request.getHeader("Authorization");
        User user = authorizationHelper.getUserFromToken(authorizationHeader);
        Project existingProject = projectRepository.findById(projectId).orElse(null);
        if (existingProject == null) {
            this.logger.error("Project Not Found");
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } else {
            existingProject.setProjectKey(existingProject.getProjectKey());
            existingProject.setProjectName(project.getProjectName());
            existingProject.setProjectType(project.getProjectType());
            existingProject.setUpdatedBy(user.getEmail());
            existingProject.setTheme(project.getTheme());
            Project updatedProject = projectRepository.save(existingProject);
            return new ResponseEntity<>(updatedProject, HttpStatus.OK);
        }
    }
    @Override
    public ResponseEntity<?> deleteProject(HttpServletRequest request,int projectId) {
        String authorizationHeader = request.getHeader("Authorization");
        User user = authorizationHelper.getUserFromToken(authorizationHeader);
        Optional<Project> projectOptional = projectRepository.findById(projectId);
        this.logger.info("Selected Project ${} : ",projectOptional);
        if(projectOptional.get().getClient_id()!=user.getClientId()){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
        }
        try {

            this.logger.info("Delete Project ${}",projectOptional);
            if (projectOptional.isPresent()) {
                List<Task> task=taskRepository.findByProject_ProjectId(projectOptional.get().getProjectId());
                this.logger.info("Tasks delete${}",task);
                List<ProjectStatus> projectStatuses=projectStatusRepository.findByProject_ProjectIdAndClientId(projectOptional.get().getProjectId(),user.getClientId());
                this.logger.info("projectStatuses delete${}",projectStatuses);
                List<Project_user> projectUsers=projectUserRepository.findProjectUsersByProjectIdAndUserId(projectOptional.get().getProjectId(), user.getUser_id());
                this.logger.info("projectUsers delete${}",projectUsers);
                List<String> taskIds = task.stream()
                        .map(Task::getId)
                        .collect(Collectors.toList());
                List<Integer> projectStatusIds = projectStatuses.stream()
                        .map(ProjectStatus::getProject_status_id)
                        .collect(Collectors.toList());
                List<Long> projectUsers1=projectUsers.stream()
                                .map(Project_user::getId)
                              .collect(Collectors.toList());
                taskRepository.deleteAllById(taskIds);
                projectStatusRepository.deleteAllById(projectStatusIds);
                projectUserRepository.deleteAllById(projectUsers1);
                projectRepository.deleteById(projectId);
                return ResponseEntity.status(HttpStatus.ACCEPTED).body("Project Deleted");
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Project Not Found");
            }
        } catch (EmptyResultDataAccessException e) {
            // Handle the case when the project is not found
            this.logger.error("Project not found with ID: " + projectId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Project not found with ID: " + projectId);
        } catch (Exception e) {
            // Handle other exceptions
            this.logger.error("Something went wrong while deleting the project", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Something went wrong while deleting the project");
        }
    }

//    @Override
//    public ResponseEntity<?> createRelation(HttpServletRequest request, ProjectNameUserName projectNameUserName){
//        String authorizationHeader = request.getHeader("Authorization");
//        User adminUser = authorizationHelper.getUserFromToken(authorizationHeader);
//            Integer clientId=adminUser.getClientId();
//            Client client=clientRepository.findById(clientId).orElseThrow();
//            Project project=projectRepository.findById(projectNameUserName.getProjectId()).orElseThrow();
//            User user=userRepository.findByEmail(projectNameUserName.getUserName()).orElseThrow();
//            Project_user projectUser=new Project_user();
//            projectUser.setUser(user);
//            projectUser.setClient(client);
//            projectUser.setProject(project);
//            projectUserRepository.save(projectUser);
//            return ResponseEntity.ok(user);
//    }

    
    @Override
    public ResponseEntity<?> createRelations(HttpServletRequest request, List<ProjectNameUserName> projectNameUserNames) {
        String authorizationHeader = request.getHeader("Authorization");
        User adminUser = authorizationHelper.getUserFromToken(authorizationHeader);
        Integer clientId = adminUser.getClientId();
        Client client = clientRepository.findById(clientId).orElseThrow();
        Project project = projectRepository.findById(projectNameUserNames.get(0).getProjectId()).orElseThrow();
        
        List<User> addedUsers = new ArrayList<>();

        for (ProjectNameUserName projectNameUserName : projectNameUserNames) {
            User user = userRepository.findByEmail(projectNameUserName.getUserName()).orElseThrow();
            Project_user projectUser = new Project_user();
            projectUser.setUser(user);
            projectUser.setClient(client);
            projectUser.setProject(project);
            projectUserRepository.save(projectUser);
            addedUsers.add(user);
        }
        
        return ResponseEntity.ok(addedUsers);
    }
}
