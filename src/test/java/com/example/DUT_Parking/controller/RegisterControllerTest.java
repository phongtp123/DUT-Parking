package com.example.DUT_Parking.controller;

import com.example.DUT_Parking.DTO.RegisterRequest;
import com.example.DUT_Parking.entity.RegisteredUsers;
import com.example.DUT_Parking.respond.GetRegisteredUsers;
import com.example.DUT_Parking.respond.RegisterRespond;
import com.example.DUT_Parking.services.RegisterService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static net.bytebuddy.matcher.ElementMatchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
    @WithMockUser(username = "admin", roles = {"ADMIN"}) // Mô phỏng ADMIN
    @DisplayName("Test Search Register User With Admin Auth - Success")
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
}
