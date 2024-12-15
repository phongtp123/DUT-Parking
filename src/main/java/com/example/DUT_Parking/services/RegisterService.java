package com.example.DUT_Parking.services;

import com.example.DUT_Parking.DTO.RegisterRequest;
import com.example.DUT_Parking.entity.RegisteredUsers;
import com.example.DUT_Parking.respond.GetRegisteredUsers;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

public interface RegisterService {

    boolean register(RegisterRequest registerRequest);
    @PreAuthorize("hasRole('ADMIN')")
    List<GetRegisteredUsers> getAllUsers();
    @PreAuthorize("hasRole('ADMIN')")
    void delete(int id);
    @PreAuthorize("hasRole('ADMIN')")
    GetRegisteredUsers search(String email);
}
