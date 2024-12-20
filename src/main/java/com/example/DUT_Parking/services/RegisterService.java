package com.example.DUT_Parking.services;

import com.example.DUT_Parking.DTO.RegisterRequest;
import com.example.DUT_Parking.respond.GetRegisteredUsers;

import java.util.List;

public interface RegisterService {

    boolean register(RegisterRequest registerRequest);
    List<GetRegisteredUsers> getAllUsers();
    void delete(int id);
    GetRegisteredUsers search(String email);
}
