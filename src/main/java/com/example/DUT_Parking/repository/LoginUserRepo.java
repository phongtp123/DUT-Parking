package com.example.DUT_Parking.repository;

import com.example.DUT_Parking.entity.LoginUsers;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LoginUserRepo extends JpaRepository<LoginUsers, String> {
    LoginUsers findByEmail(String email);
    void deleteByEmail(String email);
}