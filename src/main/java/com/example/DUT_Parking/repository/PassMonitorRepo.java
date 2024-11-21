package com.example.DUT_Parking.repository;

import com.example.DUT_Parking.entity.PassMonitor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PassMonitorRepo extends JpaRepository<PassMonitor, String> {
    PassMonitor findByEmail(String email);
}
