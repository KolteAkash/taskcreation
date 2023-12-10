package com.example.Security.repository;

import com.example.Security.model.Project;
import com.example.Security.model.Project_user;
import com.example.Security.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjectUserRepository extends JpaRepository<Project_user,Long> {

    //    @Query("SELECT pu.user FROM Project_user pu WHERE pu.project.projectId = :projectId")
//    List<User> findUsersByProjectId(@Param("projectId") int projectId);
@Query("SELECT pu.user FROM Project_user pu WHERE pu.project.projectId = :projectId AND pu.client.client_id = :clientId")
List<User> findUsersByProjectIdAndClientId(@Param("projectId") int projectId, @Param("clientId") int clientId);



    @Query("SELECT pu.project FROM Project_user pu " +
            "WHERE pu.user.user_id = :userId " +
            "AND pu.client.client_id = :clientId " +
            "AND pu.project.client_id = :clientId")
    List<Project> findProjectsByUserIdAndClientId(
            @Param("userId") String userId,
            @Param("clientId") int clientId
    );
    @Query("SELECT pu FROM Project_user pu WHERE pu.project.projectId = :projectId AND pu.user.user_id = :userId")
    List<Project_user> findProjectUsersByProjectIdAndUserId(@Param("projectId") int projectId, @Param("userId") String userId);



    @Query("SELECT pu.user FROM Project_user pu WHERE pu.client.client_id = :clientId")
    List<User> findUsersByClientId(@Param("clientId") Integer clientId);




}

