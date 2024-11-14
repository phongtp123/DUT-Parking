package com.example.DUT_Parking.services.impl;

import com.example.DUT_Parking.DTO.RegisterRequest;
import com.example.DUT_Parking.entity.RegisteredUsers;
import com.example.DUT_Parking.entity.UsersProfile;
import com.example.DUT_Parking.enums.Roles;
import com.example.DUT_Parking.exception_handling.AppException;
import com.example.DUT_Parking.exception_handling.ErrorCode;
import com.example.DUT_Parking.repository.RegisteredUserRepo;
import com.example.DUT_Parking.repository.UsersProfileRepo;
import com.example.DUT_Parking.services.RegisterService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE , makeFinal = true)
public class RegisterImpl implements RegisterService {

    RegisteredUserRepo registeredUserRepo;
    UsersProfileRepo usersProfileRepo;


    public  List<RegisteredUsers> getAllUsers() {
        return registeredUserRepo.findAll();
    }


    public boolean register(RegisterRequest register_info){
        RegisteredUsers registerUser = new RegisteredUsers();
        if (registeredUserRepo.existsByEmail(register_info.getEmail())) {
            throw new AppException(ErrorCode.USER_EXISTED);
        }
        else {
            registerUser.setEmail(register_info.getEmail());
            registerUser.setPassword(register_info.getPassword());
            var role = new HashSet<String>();
            role.add(Roles.USER.name());
            UsersProfile usersProfile = UsersProfile.builder()
                    .email(register_info.getEmail())
                    .password(register_info.getPassword())
                    .roles(role)
                    .build();
            registeredUserRepo.save(registerUser);
            usersProfileRepo.save(usersProfile);
            return true;
        }
    }

    public RegisteredUsers search(String email) {
        RegisteredUsers found_user = registeredUserRepo.findByEmail(email);
        if (found_user == null) {
            throw new AppException(ErrorCode.USER_NOT_EXISTED);
        }
        else {
            return found_user;
        }
    }

    public void delete(int id) {
        registeredUserRepo.deleteById(id);
    }
}
