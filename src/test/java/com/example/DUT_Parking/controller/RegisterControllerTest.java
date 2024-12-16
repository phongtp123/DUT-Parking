package com.example.DUT_Parking.controller;

import com.example.DUT_Parking.DTO.RegisterRequest;
import com.example.DUT_Parking.respond.GetRegisteredUsers;
import com.example.DUT_Parking.services.RegisterService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class RegisterControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RegisterService registerService;
    private RegisterRequest registerRequest;

    @BeforeEach
    void setUp() {
        registerRequest = RegisterRequest.builder()
                .email("test@gmail.com")
                .password("12345678")
                .build();
    }

    @Autowired
    private ObjectMapper objectMapper; // For converting objects to JSON

    // Test for POST /auth/register
    @Test
    @DisplayName("Test Register - Successful Registration")
    void register_Success() throws Exception {
        // Mock service behavior
        objectMapper = new ObjectMapper();
        String content = objectMapper.writeValueAsString(registerRequest);

        Mockito.when(registerService.register(any())).thenReturn(true);

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(content))
                .andExpect(status().isOk())
                .andExpect(jsonPath("code").value(1000))
                .andExpect(jsonPath("result.register_status").value(true));
    }

    @Test
    @DisplayName("Test Register - Missing Email Input")
    void register_InvalidInput_MissingEmail() throws Exception {
        // Invalid request (missing email fields)
        registerRequest.setEmail(null);
        String content = objectMapper.writeValueAsString(registerRequest);

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(content))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("code").value(1003))
                .andExpect(jsonPath("message")
                        .value("Email is invalid , the format of a valid email is :abcd1234@gmail.com "));
    }

    @Test
    @DisplayName("Test Register - Missing Password Input")
    void register_InvalidInput_MissingPassword() throws Exception {
        // Invalid request (missing password fields)
        registerRequest.setPassword(null);
        ObjectMapper objectMapper = new ObjectMapper();
        String content = objectMapper.writeValueAsString(registerRequest);

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(content))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("code").value(1004))
                .andExpect(jsonPath("message")
                        .value("Password must be at least 6 characters"));
    }

    @Test
    @DisplayName("Test Register - Password Invalid Input")
    void register_InvalidInput_InvalidPassword() throws Exception {
        // Invalid request (invalid password fields)
        registerRequest.setPassword("test");
        String content = objectMapper.writeValueAsString(registerRequest);

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(content))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("code").value(1004))
                .andExpect(jsonPath("message")
                        .value("Password must be at least 6 characters"));
    }

    @Test
    @DisplayName("Test Register - Email Invalid Input")
    void register_InvalidInput_InvalidEmail() throws Exception {
        // Invalid request (invalid password fields)
        registerRequest.setEmail("helo<3@gmail.com");
        String content = objectMapper.writeValueAsString(registerRequest);

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(content))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("code").value(1003))
                .andExpect(jsonPath("message")
                        .value("Email is invalid , the format of a valid email is :abcd1234@gmail.com "));
    }

    // Test for GET /auth/registered_users/{email}
    @Test
    @WithMockUser(username = "admin@gmail.com", password = "admin", roles = {"ADMIN"}) // Mô phỏng ADMIN
    @DisplayName("Test Search Register User With Admin Role - Success")
    public void testSearchRegisteredUser_AsAdmin_Success() throws Exception {
        // Mô phỏng dữ liệu trả về từ service
        GetRegisteredUsers mockUser = new GetRegisteredUsers();
        mockUser.setId(1L);
        mockUser.setEmail("test@gmail.com");
        mockUser.setPassword("12345678");

        Mockito.when(registerService.search(Mockito.anyString())).thenReturn(mockUser);

        // Thực thi API
        mockMvc.perform(get("/auth/registered_users/test@gmail.com")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("code").value(1000))
                .andExpect(jsonPath("result.id").value(1L))
                .andExpect(jsonPath("result.email").value("test@gmail.com"))
                .andExpect(jsonPath("result.password").value("12345678"));
    }

    @Test
    @WithAnonymousUser()
    @DisplayName("Test Search Register User Without Token or Token has been expired - Failed")
    public void testSearchRegisteredUser_AsAnonymous_Failed() throws Exception {
        // Mô phỏng dữ liệu trả về từ service
        GetRegisteredUsers mockUser = new GetRegisteredUsers();
        mockUser.setId(1L);
        mockUser.setEmail("test@gmail.com");
        mockUser.setPassword("12345678");

        Mockito.when(registerService.search(Mockito.anyString())).thenReturn(mockUser);

        // Thực thi API
        mockMvc.perform(get("/auth/registered_users/test@gmail.com")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("code").value(1006))
                .andExpect(jsonPath("message").value("Unauthenticated"));
    }

    @Test
    @WithMockUser(username = "test@gmail.com")
    @DisplayName("Test Search Register User With User Role - Failed")
    public void testSearchRegisteredUser_AsUser_Failed() throws Exception {
        // Mô phỏng dữ liệu trả về từ service
        GetRegisteredUsers mockUser = new GetRegisteredUsers();
        mockUser.setId(1L);
        mockUser.setEmail("test@gmail.com");
        mockUser.setPassword("12345678");

        Mockito.when(registerService.search(Mockito.anyString())).thenReturn(mockUser);

        // Thực thi API
        mockMvc.perform(get("/auth/registered_users/test@gmail.com")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("code").value(1007))
                .andExpect(jsonPath("message").value("You do not have permission"));
    }

    // Test for GET /auth/registered_users
    @Test
    @WithMockUser(username = "admin@gmail.com" , roles = {"ADMIN"})
    @DisplayName("Test Get All Register User With Admin Role - Success")
    public void testGetAllRegisteredUser_AsAdmin_Success() throws Exception {
        // Mô phỏng dữ liệu trả về từ service
        GetRegisteredUsers mockUser = new GetRegisteredUsers();
        mockUser.setId(1L);
        mockUser.setEmail("test@gmail.com");
        mockUser.setPassword("12345678");

        List<GetRegisteredUsers> listMockUser = Collections.singletonList(mockUser);

        Mockito.when(registerService.getAllUsers()).thenReturn(listMockUser);

        // Thực thi API
        mockMvc.perform(get("/auth/registered_users")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].email").value("test@gmail.com"))
                .andExpect(jsonPath("$[0].password").value("12345678"));
    }

    @Test
    @WithMockUser(username = "test@gmail.com")
    @DisplayName("Test Get All Register User With User Role - Success")
    public void testGetAllRegisteredUser_AsUser_Failed() throws Exception {
        // Mô phỏng dữ liệu trả về từ service
        GetRegisteredUsers mockUser = new GetRegisteredUsers();
        mockUser.setId(1L);
        mockUser.setEmail("test@gmail.com");
        mockUser.setPassword("12345678");

        List<GetRegisteredUsers> listMockUser = Collections.singletonList(mockUser);

        Mockito.when(registerService.getAllUsers()).thenReturn(listMockUser);

        // Thực thi API
        mockMvc.perform(get("/auth/registered_users")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("code").value(1007))
                .andExpect(jsonPath("message").value("You do not have permission"));
    }

    @Test
    @WithAnonymousUser
    @DisplayName("Test Get All Register User With Anonymous Account - Success")
    public void testGetAllRegisteredUser_AsAnonymous_Failed() throws Exception {
        // Mô phỏng dữ liệu trả về từ service
        GetRegisteredUsers mockUser = new GetRegisteredUsers();
        mockUser.setId(1L);
        mockUser.setEmail("test@gmail.com");
        mockUser.setPassword("12345678");

        List<GetRegisteredUsers> listMockUser = Collections.singletonList(mockUser);

        Mockito.when(registerService.getAllUsers()).thenReturn(listMockUser);

        // Thực thi API
        mockMvc.perform(get("/auth/registered_users")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("code").value(1006))
                .andExpect(jsonPath("message").value("Unauthenticated"));
    }

    // Test for DELETE /auth/registered_users/{id}
    @Test
    @WithMockUser(username = "admin@gmail.com" ,password = "admin", roles = {"ADMIN"})
    @DisplayName("Test Delete Register User With Admin Role - Success")
    public void testDeleteRegisteredUser_AsAdmin_Success() throws Exception {
        // Mô phỏng dữ liệu trả về từ service

        String respond = "User has been deleted";

        Mockito.doNothing().when(registerService).delete(Mockito.anyInt());

        // Thực thi API
        mockMvc.perform(delete("/auth/registered_users/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(respond));
    }

    @Test
    @WithMockUser(username = "test@gmail.com")
    @DisplayName("Test Delete Register User With User Role - Failed")
    public void testDeleteRegisteredUser_AsUser_Failed() throws Exception {
        // Mô phỏng dữ liệu trả về từ service

        String respond = "User has been deleted";

        Mockito.doNothing().when(registerService).delete(Mockito.anyInt());

        // Thực thi API
        mockMvc.perform(delete("/auth/registered_users/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("code").value(1007))
                .andExpect(jsonPath("message").value("You do not have permission"));
    }

    @Test
    @WithAnonymousUser
    @DisplayName("Test Delete Register User With Anonymous Account - Failed")
    public void testDeleteRegisteredUser_AsAnonymous_Failed() throws Exception {
        // Mô phỏng dữ liệu trả về từ service

        String respond = "User has been deleted";

        Mockito.doNothing().when(registerService).delete(Mockito.anyInt());

        // Thực thi API
        mockMvc.perform(delete("/auth/registered_users/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("code").value(1006))
                .andExpect(jsonPath("message").value("Unauthenticated"));
    }
}
