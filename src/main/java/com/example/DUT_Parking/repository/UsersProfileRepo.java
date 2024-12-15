package com.example.DUT_Parking.repository;

import com.example.DUT_Parking.entity.UsersProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UsersProfileRepo extends JpaRepository<UsersProfile, String> {
    List<UsersProfile> findByMSSV(String MSSV);
    UsersProfile findByHovaten(String hovaten);
    void deleteById(String MSSV);
    UsersProfile findByEmail(String email);
}
