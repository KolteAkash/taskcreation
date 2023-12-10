package com.example.Security.repository;

import com.example.Security.model.ProjectStatus;
import com.example.Security.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task,String> {
    List<Task> findByProject_ProjectId(int projectId);
}
