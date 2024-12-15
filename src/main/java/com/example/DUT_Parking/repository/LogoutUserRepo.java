package com.example.DUT_Parking.repository;

import com.example.DUT_Parking.entity.LogoutUsers;
import com.example.DUT_Parking.entity.RegisteredUsers;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LogoutUserRepo extends JpaRepository<LogoutUsers, String> {
    boolean existsByRegisteredUsers(RegisteredUsers registeredUsers);
}
