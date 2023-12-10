package com.example.Security.repository;

import com.example.Security.model.Epic;
import com.example.Security.model.Initiative;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EpicRepository extends JpaRepository<Epic, Long> {
    @Query("SELECT e FROM Epic e WHERE e.initiative IN :initiatives")
    List<Epic> findEpicsByInitiatives(@Param("initiatives") List<Initiative> initiatives);
}
