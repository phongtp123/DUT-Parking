package com.example.DUT_Parking.controller;

import com.example.DUT_Parking.DTO.RegisterRequest;
import com.example.DUT_Parking.respond.GetRegisteredUsers;
import com.example.DUT_Parking.services.RegisterService;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.qameta.allure.Feature;
import io.qameta.allure.Link;
import io.qameta.allure.TmsLink;
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
@DisplayName("Register Controller Test")
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
    @Feature("Register")
    @Test
    @TmsLink("14")
    @DisplayName("Test Register - Successful Registration")
    @Link(name = "AUTH-14" , type = "task")
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

    @Feature("Register")
    @Test
    @TmsLink("15")
    @DisplayName("Test Register - Missing Email Input")
    @Link(name = "AUTH-15" , type = "task")
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

    @Feature("Register")
    @Test
    @TmsLink("16")
    @DisplayName("Test Register - Missing Password Input")
    @Link(name = "AUTH-16" , type = "task")
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

    @Feature("Register")
    @Test
    @TmsLink("17")
    @DisplayName("Test Register - Password Invalid Input")
    @Link(name = "AUTH-17" , type = "task")
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

    @Feature("Register")
    @Test
    @TmsLink("18")
    @DisplayName("Test Register - Email Invalid Input")
    @Link(name = "AUTH-18" , type = "task")
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
    @Feature("Search Register User By Email")
    @Test
    @WithMockUser(username = "admin@gmail.com", password = "admin", roles = {"ADMIN"}) // Mô phỏng ADMIN
    @TmsLink("19")
    @DisplayName("Test Search Register User With Admin Role - Success")
    @Link(name = "AUTH-19" , type = "task")
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

    @Feature("Search Register User By Email")
    @Test
    @WithAnonymousUser()
    @TmsLink("20")
    @DisplayName("Test Search Register User Without Token or Token has been expired - Failed")
    @Link(name = "AUTH-20" , type = "task")
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

    @Feature("Search Register User By Email")
    @Test
    @WithMockUser(username = "test@gmail.com")
    @TmsLink("21")
    @DisplayName("Test Search Register User With User Role - Failed")
    @Link(name = "AUTH-21" , type = "task")
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
    @Feature("Get All Register User")
    @Test
    @WithMockUser(username = "admin@gmail.com" , roles = {"ADMIN"})
    @TmsLink("22")
    @DisplayName("Test Get All Register User With Admin Role - Success")
    @Link(name = "AUTH-22" , type = "task")
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

    @Feature("Get All Register User")
    @Test
    @WithMockUser(username = "test@gmail.com")
    @TmsLink("23")
    @DisplayName("Test Get All Register User With User Role - Success")
    @Link(name = "AUTH-23" , type = "task")
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

    @Feature("Get All Register User")
    @Test
    @WithAnonymousUser
    @TmsLink("24")
    @DisplayName("Test Get All Register User With Anonymous Account - Success")
    @Link(name = "AUTH-24" , type = "task")
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
    @Feature("Delete Register User By Id")
    @Test
    @WithMockUser(username = "admin@gmail.com" ,password = "admin", roles = {"ADMIN"})
    @TmsLink("25")
    @DisplayName("Test Delete Register User With Admin Role - Success")
    @Link(name = "AUTH-25" , type = "task")
    public void testDeleteRegisteredUser_AsAdmin_Success() throws Exception {
        // Mô phỏng dữ liệu trả về từ service

        String respond = "User has been deleted";

        Mockito.doNothing().when(registerService).delete(Mockito.anyLong());

        // Thực thi API
        mockMvc.perform(delete("/auth/registered_users/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(respond));
    }

    @Feature("Delete Register User By Id")
    @Test
    @WithMockUser(username = "test@gmail.com")
    @TmsLink("26")
    @DisplayName("Test Delete Register User With User Role - Failed")
    @Link(name = "AUTH-26" , type = "task")
    public void testDeleteRegisteredUser_AsUser_Failed() throws Exception {
        // Mô phỏng dữ liệu trả về từ service

        String respond = "User has been deleted";

        Mockito.doNothing().when(registerService).delete(Mockito.anyLong());

        // Thực thi API
        mockMvc.perform(delete("/auth/registered_users/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("code").value(1007))
                .andExpect(jsonPath("message").value("You do not have permission"));
    }

    @Feature("Delete Register User By Id")
    @Test
    @WithAnonymousUser
    @TmsLink("27")
    @DisplayName("Test Delete Register User With Anonymous Account - Failed")
    @Link(name = "AUTH-27" , type = "task")
    public void testDeleteRegisteredUser_AsAnonymous_Failed() throws Exception {
        // Mô phỏng dữ liệu trả về từ service

        String respond = "User has been deleted";

        Mockito.doNothing().when(registerService).delete(Mockito.anyLong());

        // Thực thi API
        mockMvc.perform(delete("/auth/registered_users/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("code").value(1006))
                .andExpect(jsonPath("message").value("Unauthenticated"));
    }
}
