package com.example.Security.repository;

import com.example.Security.model.SuperAdmin_client;
import lombok.extern.java.Log;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface SuperAdminClientRepository extends JpaRepository<SuperAdmin_client, Long> {
//    Optional<SuperAdmin_client> findBySuperAdmin(String user_id);
}
