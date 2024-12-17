package com.example.DUT_Parking.controller;

import com.example.DUT_Parking.DTO.AuthenticationRequest;
import com.example.DUT_Parking.DTO.IntrospectLoginToken;
import com.example.DUT_Parking.DTO.LogoutRequest;
import com.example.DUT_Parking.respond.*;
import com.example.DUT_Parking.services.AuthenticationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.DisplayName;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;

import java.sql.Date;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class AuthenticationControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthenticationService authenticationService;

    @Autowired
    private ObjectMapper objectMapper;

    //Test for POST /auth/login
    @Test
    @DisplayName("All Clear - Login Successful")
    void login_Success() throws Exception {
        AuthenticationRequest authenticationRequest = AuthenticationRequest.builder()
                                                            .email("test@gmail.com")
                                                            .password("password")
                                                            .build();;

        AuthenticationRespond authenticationRespond = AuthenticationRespond.builder()
                                                                .authenticated(true)
                                                                .token("abcxyz")
                                                                .build();
        APIRespond<AuthenticationRespond> apiRespond = APIRespond.<AuthenticationRespond>builder()
                                                                    .result(authenticationRespond)
                                                                    .build();
        objectMapper = new ObjectMapper();
        String content = objectMapper.writeValueAsString(authenticationRequest);

        Mockito.when(authenticationService.authenticated(any())).thenReturn(apiRespond.getResult());

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(content))
                .andExpect(status().isOk())
                .andExpect(jsonPath("result.authenticated").value(true))
                .andExpect(jsonPath("result.token").value("abcxyz"));
    }

    @Test
    @DisplayName("Null Email Input - Login Failed")
    void loginWithNullEmail_Failed() throws Exception {
        AuthenticationRequest authenticationRequest = AuthenticationRequest.builder()
                .email(null)
                .password("password")
                .build();;

        objectMapper = new ObjectMapper();
        String content = objectMapper.writeValueAsString(authenticationRequest);

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(content))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("code").value(1003))
                .andExpect(jsonPath("message")
                        .value("Email is invalid , the format of a valid email is :abcd1234@gmail.com ")
                );
    }

    @Test
    @DisplayName("Null Password Input - Login Failed")
    void loginWithNullPassword_Failed() throws Exception {
        AuthenticationRequest authenticationRequest = AuthenticationRequest.builder()
                .email("test@gmail.com")
                .password(null)
                .build();;

        objectMapper = new ObjectMapper();
        String content = objectMapper.writeValueAsString(authenticationRequest);

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(content))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("code").value(1004))
                .andExpect(jsonPath("message")
                        .value("Password must be at least 6 characters")
                );
    }

    @Test
    @DisplayName("Invalid Password Input - Login Failed")
    void loginWithInvalidPassword_Failed() throws Exception {
        AuthenticationRequest authenticationRequest = AuthenticationRequest.builder()
                .email("test@gmail.com")
                .password("test")
                .build();;

        objectMapper = new ObjectMapper();
        String content = objectMapper.writeValueAsString(authenticationRequest);

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(content))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("code").value(1004))
                .andExpect(jsonPath("message")
                        .value("Password must be at least 6 characters")
                );
    }

    @Test
    @DisplayName("Invalid Email Input - Login Failed")
    void loginWithInvalidEmail_Failed() throws Exception {
        AuthenticationRequest authenticationRequest = AuthenticationRequest.builder()
                .email("test<3@gmail.com")
                .password("password")
                .build();;

        objectMapper = new ObjectMapper();
        String content = objectMapper.writeValueAsString(authenticationRequest);

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(content))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("code").value(1003))
                .andExpect(jsonPath("message")
                        .value("Email is invalid , the format of a valid email is :abcd1234@gmail.com ")
                );
    }

    //Test for GET /auth/login-users
    @Test
    @DisplayName("With Admin Role - Get All Login Users Success")
    @WithMockUser(username = "admin@gmail.com" , roles = {"ADMIN"})
    void getAllLoginUsers_WithAdminRole_Success() throws Exception {
        GetLoginUsers getLoginUsers = GetLoginUsers.builder()
                .id(1L)
                .email("test@gmail.com")
                .build();
        List<GetLoginUsers> getLoginUsersList = Collections.singletonList(getLoginUsers);

        Mockito.when(authenticationService.getAllUsers()).thenReturn(getLoginUsersList);

        mockMvc.perform(get("/auth/login-users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].email").value("test@gmail.com"));
    }

    @Test
    @DisplayName("With User Role - Get All Login Users Forbidden Failed")
    @WithMockUser(username = "test@gmail.com")
    void getAllLoginUsers_WithUserRole_Failed() throws Exception {
        GetLoginUsers getLoginUsers = GetLoginUsers.builder()
                .id(1L)
                .email("test@gmail.com")
                .build();
        List<GetLoginUsers> getLoginUsersList = Collections.singletonList(getLoginUsers);

        Mockito.when(authenticationService.getAllUsers()).thenReturn(getLoginUsersList);

        mockMvc.perform(get("/auth/login-users"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("code").value(1007))
                .andExpect(jsonPath("message").value("You do not have permission"));
    }

    @Test
    @DisplayName("Anonymous Account or Expired Token - Get All Login Users Unauthorized Failed")
    @WithAnonymousUser
    void getAllLoginUsers_WithAnonymous_Failed() throws Exception {
        GetLoginUsers getLoginUsers = GetLoginUsers.builder()
                .id(1L)
                .email("test@gmail.com")
                .build();
        List<GetLoginUsers> getLoginUsersList = Collections.singletonList(getLoginUsers);

        Mockito.when(authenticationService.getAllUsers()).thenReturn(getLoginUsersList);

        mockMvc.perform(get("/auth/login-users"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("code").value(1006))
                .andExpect(jsonPath("message").value("Unauthenticated"));
    }

    //Test for POST /auth/introspect
    @Test
    @DisplayName("Introspect Valid Token - Success")
    void introspectValidToken_Success() throws Exception {
        IntrospectLoginToken introspectLoginToken = IntrospectLoginToken.builder()
                .token("abcxyz")
                .build();
        IntrospectRespond introspectRespond = IntrospectRespond.builder()
                .valid(true)
                .valid_expired(true)
                .build();
        APIRespond<IntrospectRespond> apiRespond = APIRespond.<IntrospectRespond>builder()
                .result(introspectRespond)
                .build();

        objectMapper = new ObjectMapper();
        String content = objectMapper.writeValueAsString(introspectLoginToken);

        Mockito.when(authenticationService.introspect(any())).thenReturn(apiRespond.getResult());

        mockMvc.perform(post("/auth/introspect")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(content))
                .andExpect(status().isOk())
                .andExpect(jsonPath("result.valid").value(true))
                .andExpect(jsonPath("result.valid_expired").value(true));
    }

    @Test
    @DisplayName("Introspect InValid Token - Failed")
    void introspectInValidToken_Success() throws Exception {
        IntrospectLoginToken introspectLoginToken = IntrospectLoginToken.builder()
                .token("abcxyz")
                .build();
        IntrospectRespond introspectRespond = IntrospectRespond.builder()
                .valid(false)
                .valid_expired(false)
                .build();
        APIRespond<IntrospectRespond> apiRespond = APIRespond.<IntrospectRespond>builder()
                .result(introspectRespond)
                .build();

        objectMapper = new ObjectMapper();
        String content = objectMapper.writeValueAsString(introspectLoginToken);

        Mockito.when(authenticationService.introspect(any())).thenReturn(apiRespond.getResult());

        mockMvc.perform(post("/auth/introspect")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(content))
                .andExpect(status().isOk())
                .andExpect(jsonPath("result.valid").value(false))
                .andExpect(jsonPath("result.valid_expired").value(false));
    }

    //Test for POST /auth/logout
    @Test
    @DisplayName("All Clear - Logout Success")
    void logout_Success() throws Exception {
        LogoutRequest logoutRequest = LogoutRequest.builder()
                .token("abcxyz")
                .build();

        objectMapper = new ObjectMapper();
        String content = objectMapper.writeValueAsString(logoutRequest);

        Mockito.doNothing().when(authenticationService).logout(any());

        mockMvc.perform(post("/auth/logout")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(content))
                .andExpect(status().isOk());
    }

    //Test for GET /auth/logout-users
    @Test
    @DisplayName("With Admin Role - Get All Logout Users Success")
    @WithMockUser(username = "admin@gmail.com" , roles = {"ADMIN"})
    void getAllLogoutUsers_WithAdminRole_Success() throws Exception {
        var localDate = LocalDate.of(2025,12,31);
        var expiryDate = Date.valueOf(localDate);
        GetLogoutUsers getLogoutUsers = GetLogoutUsers.builder()
                .id(1L)
                .email("test@gmail.com")
                .expiryDate(expiryDate)
                .build();

        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        String content = objectMapper.writeValueAsString(getLogoutUsers);

        List<GetLogoutUsers> getLogoutUsersList = Collections.singletonList(getLogoutUsers);

        Mockito.when(authenticationService.getAllLogoutUsers()).thenReturn(getLogoutUsersList);

        mockMvc.perform(get("/auth/logout-users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].email").value("test@gmail.com"))
                .andExpect(jsonPath("$[0].expiryDate").value(expiryDate.toString()));
    }

    @Test
    @DisplayName("With User Role - Get All Logout Users Forbidden Failed")
    @WithMockUser(username = "test@gmail.com")
    void getAllLogoutUsers_WithUserRole_Failed() throws Exception {
        var localDate = LocalDate.of(2025,12,31);
        var expiryDate = Date.valueOf(localDate);
        GetLogoutUsers getLogoutUsers = GetLogoutUsers.builder()
                .id(1L)
                .email("test@gmail.com")
                .expiryDate(expiryDate)
                .build();

        List<GetLogoutUsers> getLogoutUsersList = Collections.singletonList(getLogoutUsers);

        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        String content = objectMapper.writeValueAsString(getLogoutUsers);

        Mockito.when(authenticationService.getAllLogoutUsers()).thenReturn(getLogoutUsersList);

        mockMvc.perform(get("/auth/logout-users"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("code").value(1007))
                .andExpect(jsonPath("message").value("You do not have permission"));
    }

    @Test
    @DisplayName("Anonymous Account or Expired Token - Get All Logout Users Unauthorized Failed")
    @WithAnonymousUser
    void getAllLogoutUsers_WithAnonymous_Failed() throws Exception {
        var localDate = LocalDate.of(2025,12,31);
        var expiryDate = Date.valueOf(localDate);
        GetLogoutUsers getLogoutUsers = GetLogoutUsers.builder()
                .id(1L)
                .email("test@gmail.com")
                .expiryDate(expiryDate)
                .build();

        List<GetLogoutUsers> getLogoutUsersList = Collections.singletonList(getLogoutUsers);

        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        String content = objectMapper.writeValueAsString(getLogoutUsers);

        Mockito.when(authenticationService.getAllLogoutUsers()).thenReturn(getLogoutUsersList);

        mockMvc.perform(get("/auth/logout-users"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("code").value(1006))
                .andExpect(jsonPath("message").value("Unauthenticated"));
    }


}
