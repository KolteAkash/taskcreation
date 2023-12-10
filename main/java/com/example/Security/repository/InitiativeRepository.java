package com.example.Security.repository;

import com.example.Security.model.Initiative;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InitiativeRepository extends JpaRepository<Initiative, Integer>{
    @Query("SELECT i FROM Initiative i WHERE i.projectType.projectType_id = :projectTypeId")
    List<Initiative> findByProjectTypeId(@Param("projectTypeId") int projectTypeId);

}
