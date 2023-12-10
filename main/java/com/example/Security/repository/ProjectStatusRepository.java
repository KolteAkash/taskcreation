package com.example.Security.repository;

import com.example.Security.model.ProjectStatus;
import com.example.Security.model.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjectStatusRepository extends JpaRepository<ProjectStatus,Integer> {
    List<ProjectStatus> findByProject_ProjectIdAndClientId(int projectId,int clientId);
     ProjectStatus findByStatus_Status(String status);
     ProjectStatus findByStatus(Status status);


}
