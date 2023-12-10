package com.example.Security.repository;

import com.example.Security.model.Client_user;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClientUserRepository extends JpaRepository<Client_user,Long> {

}
