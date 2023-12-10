package com.example.Security.repository;

import com.example.Security.model.ProjectType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ProjectTypeRepository extends JpaRepository<ProjectType,Integer> {
    @Query("SELECT p.projectType_id FROM ProjectType p WHERE p.projectType = :projectType")
    Integer findProjectTypeIdByProjectType(@Param("projectType") String projectType);
}
