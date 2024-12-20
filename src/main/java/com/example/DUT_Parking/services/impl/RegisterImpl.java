package com.example.DUT_Parking.services.impl;

import com.example.DUT_Parking.DTO.RegisterRequest;
import com.example.DUT_Parking.entity.RegisteredUsers;
import com.example.DUT_Parking.entity.UsersProfile;
import com.example.DUT_Parking.enums.Roles;
import com.example.DUT_Parking.exception_handling.AppException;
import com.example.DUT_Parking.exception_handling.ErrorCode;
import com.example.DUT_Parking.repository.RegisteredUserRepo;
import com.example.DUT_Parking.repository.UsersProfileRepo;
import com.example.DUT_Parking.respond.GetRegisteredUsers;
import com.example.DUT_Parking.services.RegisterService;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE , makeFinal = true)
public class RegisterImpl implements RegisterService {

    RegisteredUserRepo registeredUserRepo;
    UsersProfileRepo usersProfileRepo;


    public  List<GetRegisteredUsers> getAllUsers() {

        List<RegisteredUsers> registeredUsers = registeredUserRepo.findAll();

        return registeredUsers.stream().map(registeredUser -> {
            GetRegisteredUsers getRegisteredUsers = new GetRegisteredUsers();
            getRegisteredUsers.setId(registeredUser.getId());
            getRegisteredUsers.setEmail(registeredUser.getEmail());
            getRegisteredUsers.setPassword(registeredUser.getPassword());
            return getRegisteredUsers;
        }).collect(Collectors.toList());
    }


    public boolean register(RegisterRequest register_info){
        RegisteredUsers registerUser = new RegisteredUsers();
        registerUser.setEmail(register_info.getEmail());
        registerUser.setPassword(register_info.getPassword());
        var role = new HashSet<String>();
        role.add(Roles.USER.name());
        try {
            UsersProfile usersProfile = UsersProfile.builder()
                    .email(register_info.getEmail())
                    .password(register_info.getPassword())
                    .roles(role)
                    .build();
            registeredUserRepo.save(registerUser);
            usersProfileRepo.save(usersProfile);
        } catch (DataIntegrityViolationException e) {
            throw new AppException(ErrorCode.EMAIL_EXISTED);
        }
        return true;
    }

    public GetRegisteredUsers search(String email) {
        RegisteredUsers found_user = registeredUserRepo.findByEmail(email);
        if (found_user == null) {
            throw new AppException(ErrorCode.USER_NOT_EXISTED);
        }
        else {
            return GetRegisteredUsers.builder()
                    .id(found_user.getId())
                    .email(found_user.getEmail())
                    .password(found_user.getPassword())
                    .build();
        }
    }

    @Transactional
    public void delete(Long id) {
        try {
            registeredUserRepo.deleteById(id);
        } catch (EmptyResultDataAccessException e) {
            throw new AppException(ErrorCode.USER_NOT_EXISTED);
        }
    }
}
