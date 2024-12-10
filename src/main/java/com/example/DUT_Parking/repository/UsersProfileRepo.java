package com.example.DUT_Parking.repository;

import com.example.DUT_Parking.entity.UsersProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UsersProfileRepo extends JpaRepository<UsersProfile, String> {
    UsersProfile findByEmail(String email);
    UsersProfile findByHovaten(String hovaten);
    void deleteById(Long id);
}
