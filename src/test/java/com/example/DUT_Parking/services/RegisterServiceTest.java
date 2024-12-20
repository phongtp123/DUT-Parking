package com.example.DUT_Parking.services;

import com.example.DUT_Parking.DTO.RegisterRequest;
import com.example.DUT_Parking.entity.RegisteredUsers;
import com.example.DUT_Parking.entity.UsersProfile;
import com.example.DUT_Parking.exception_handling.AppException;
import com.example.DUT_Parking.repository.RegisteredUserRepo;
import com.example.DUT_Parking.repository.UsersProfileRepo;
import com.example.DUT_Parking.respond.GetRegisteredUsers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class RegisterServiceTest {
    @Autowired
    private RegisterService registerService;

    @MockBean
    private RegisteredUserRepo registeredUserRepo;
    @MockBean
    private UsersProfileRepo usersProfileRepo;

    // Test for func getAllUsers()
    @Test
    @DisplayName("Test get all Registered Users - Success return a list")
    void testGetAllRegisteredUsers() {

        RegisteredUsers registeredUsers = RegisteredUsers.builder()
                .id(1L)
                .email("test@gmail.com")
                .password("password")
                .build();

        List<RegisteredUsers> registeredUsersList = Collections.singletonList(registeredUsers);

        Mockito.when(registeredUserRepo.findAll()).thenReturn(registeredUsersList);

        var respond = registerService.getAllUsers();

        assertEquals(1L , respond.get(0).getId());
        assertEquals("test@gmail.com", respond.get(0).getEmail());
        assertEquals("password", respond.get(0).getPassword());
    }

    // Test for func register()
    @Test
    @DisplayName("Test register function - register success")
    void testRegister_Success() {
        RegisterRequest registerRequest = RegisterRequest.builder()
                .email("test@gmail.com")
                .password("password")
                .build();

        RegisteredUsers registeredUsers = RegisteredUsers.builder()
                .id(1L)
                .email(registerRequest.getEmail())
                .password(registerRequest.getPassword())
                .build();

        UsersProfile usersProfile = UsersProfile.builder()
                .id(1L)
                .email(registerRequest.getEmail())
                .password(registerRequest.getPassword())
                .roles(Collections.singleton("USER"))
                .build();

        Mockito.when(registeredUserRepo.save(registeredUsers)).thenReturn(registeredUsers);
        Mockito.when(usersProfileRepo.save(usersProfile)).thenReturn(usersProfile);

        boolean result = registerService.register(registerRequest);

        assertTrue(result);
    }

    @Test
    @DisplayName("Test register function - EMAIL_EXIST failed")
    void testRegister_Failed() {
        RegisterRequest registerRequest = RegisterRequest.builder()
                .email("test@gmail.com")
                .password("password")
                .build();

        RegisteredUsers registeredUsers = RegisteredUsers.builder()
                .id(1L)
                .email("test@gmail.com")
                .password("password")
                .build();


        Mockito.when(registeredUserRepo.save(registeredUsers)).thenReturn(registeredUsers);

        var exception = assertThrows(AppException.class, () -> registerService.register(registerRequest));

        assertEquals( 1002 , exception.getErrorCode().getCode() );
        assertEquals("Email existed!" , exception.getMessage());
    }
}
