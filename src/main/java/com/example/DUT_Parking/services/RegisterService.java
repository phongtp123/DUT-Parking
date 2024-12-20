package com.example.DUT_Parking.services;

import com.example.DUT_Parking.DTO.RegisterRequest;
import com.example.DUT_Parking.exception_handling.AppException;
import com.example.DUT_Parking.respond.GetRegisteredUsers;

import java.util.List;

public interface RegisterService {

    boolean register(RegisterRequest registerRequest);
    List<GetRegisteredUsers> getAllUsers();
    void delete(Long id);
    GetRegisteredUsers search(String email);
}
