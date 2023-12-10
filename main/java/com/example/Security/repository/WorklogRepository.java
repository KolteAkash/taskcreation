package com.example.Security.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.Security.model.Worklog;

@Repository
public interface WorklogRepository extends JpaRepository<Worklog, Long> {

	List<Worklog> findByTaskId(String taskId);
}