package com.example.DUT_Parking.services;

import com.example.DUT_Parking.DTO.PassRequest;
import com.example.DUT_Parking.entity.PassMonitor;
import org.springframework.security.access.prepost.PreAuthorize;

import java.text.ParseException;
import java.util.List;

public interface PassMonitorService {
    void HandlePassData (PassRequest request) throws ParseException;
    @PreAuthorize("hasRole('ADMIN')")
    List<PassMonitor> getAllPassData();
}
