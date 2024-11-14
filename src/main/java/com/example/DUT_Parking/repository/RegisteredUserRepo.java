package com.example.DUT_Parking.repository;

import com.example.DUT_Parking.entity.RegisteredUsers;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RegisteredUserRepo extends JpaRepository<RegisteredUsers, String> {
    boolean existsByEmail(String email);
    RegisteredUsers findByEmail(String email);
    RegisteredUsers findByPassword(String password);
    void deleteById(int id);
}
