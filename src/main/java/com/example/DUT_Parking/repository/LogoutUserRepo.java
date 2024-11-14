package com.example.DUT_Parking.repository;

import com.example.DUT_Parking.entity.LogoutUsers;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LogoutUserRepo extends JpaRepository<LogoutUsers, String> {
    @Override
    boolean existsById(String s);
    LogoutUsers findFirstBySubject(String subject);
}
