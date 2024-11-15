package com.example.DUT_Parking.services;

import com.example.DUT_Parking.DTO.RegisterRequest;
import com.example.DUT_Parking.entity.RegisteredUsers;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

public interface RegisterService {

    boolean register(RegisterRequest registerRequest);
    @PreAuthorize("hasRole('ADMIN')")
    List<RegisteredUsers> getAllUsers();
    @PreAuthorize("hasRole('ADMIN')")
    void delete(int id);
    @PreAuthorize("hasRole('ADMIN')")
    RegisteredUsers search(String email);
}
