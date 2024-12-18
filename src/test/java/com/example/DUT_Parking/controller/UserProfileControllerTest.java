package com.example.DUT_Parking.controller;

import com.example.DUT_Parking.DTO.AuthenticationRequest;
import com.example.DUT_Parking.DTO.IntrospectLoginToken;
import com.example.DUT_Parking.DTO.LogoutRequest;
import com.example.DUT_Parking.DTO.UpdateRequest;
import com.example.DUT_Parking.respond.*;
import com.example.DUT_Parking.services.AdminServices;
import com.example.DUT_Parking.services.AuthenticationService;
import com.example.DUT_Parking.services.UserServices;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Qualifier;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class UserProfileControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    @Qualifier("updateUserProfileImpl")
    private UserServices userServices;

    @MockBean
    @Qualifier("updateUserProfileImpl")
    private AdminServices adminServices;

    @Autowired
    private ObjectMapper objectMapper;

    //Test for GET /profile/my_profile
    @Test
    @DisplayName("Test Get User Profile With Valid Token - Get Success")
    @WithMockUser(username = "test@gmail.com")
    void testGetUserProfile_ValidToken_Success() throws Exception {
        var localDate = LocalDate.of(2025,12,31);
        var dob = Date.valueOf(localDate);
        GetProfileRespond getProfileRespond = GetProfileRespond.builder()
                .MSSV("12345678")
                .email("test@gmail.com")
                .hovaten("Nguyễn Thanh Phong")
                .sdt("123456789")
                .diachi("test")
                .quequan("test")
                .gioitinh("test")
                .dob(dob)
                .sodu(100000)
                .build();
        APIRespond<GetProfileRespond> apiRespond = APIRespond.<GetProfileRespond>builder()
                .result(getProfileRespond)
                .build();

        Mockito.when(userServices.GetUserProfile()).thenReturn(apiRespond.getResult());

        mockMvc.perform(get("/profile/my_profile"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("result.mssv").value("12345678"))
                .andExpect(jsonPath("result.email").value("test@gmail.com"))
                .andExpect(jsonPath("result.hovaten").value("Nguyễn Thanh Phong"))
                .andExpect(jsonPath("result.sdt").value("123456789"))
                .andExpect(jsonPath("result.diachi").value("test"))
                .andExpect(jsonPath("result.quequan").value("test"))
                .andExpect(jsonPath("result.gioitinh").value("test"))
                .andExpect(jsonPath("result.dob").value(dob.toString()));
    }

    @Test
    @DisplayName("Test Get User Profile With Expired Token - Get Failed")
    @WithAnonymousUser
    void testGetUserProfile_ExpiredToken_Failed() throws Exception {
        var localDate = LocalDate.of(2025,12,31);
        var dob = Date.valueOf(localDate);
        GetProfileRespond getProfileRespond = GetProfileRespond.builder()
                .MSSV("12345678")
                .email("test@gmail.com")
                .hovaten("Nguyễn Thanh Phong")
                .sdt("123456789")
                .diachi("test")
                .quequan("test")
                .gioitinh("test")
                .dob(dob)
                .sodu(100000)
                .build();
        APIRespond<GetProfileRespond> apiRespond = APIRespond.<GetProfileRespond>builder()
                .result(getProfileRespond)
                .build();

        Mockito.when(userServices.GetUserProfile()).thenReturn(apiRespond.getResult());

        mockMvc.perform(get("/profile/my_profile"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("code").value(1006))
                .andExpect(jsonPath("message").value("Unauthenticated"));
    }

    //Test for PUT /profile/my_profile/update
    @Test
    @DisplayName("Test Update User Profile With Valid Token - Update Success")
    @WithMockUser(username = "test@gmail.com")
    void testUpdateUserProfile_ValidToken_Success() throws Exception {
        var localDate = LocalDate.of(2025,12,31);
        var dob = Date.valueOf(localDate);
        UpdateRequest updateRequest = UpdateRequest.builder()
                .MSSV("12345678")
                .hovaten("Nguyễn Thanh Phong")
                .sdt("123456789")
                .diachi("test")
                .quequan("test")
                .gioitinh("test")
                .dob(dob)
                .build();
        UpdateRespond updateRespond = UpdateRespond.builder()
                .update_status(true)
                .message("Update user profile successfully")
                .build();

        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        String content = objectMapper.writeValueAsString(updateRequest);

        APIRespond<UpdateRespond> apiRespond = APIRespond.<UpdateRespond>builder()
                .result(updateRespond)
                .build();

        Mockito.when(userServices.UpdateProfile(any())).thenReturn(apiRespond.getResult());

        mockMvc.perform(put("/profile/my_profile/update")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(content))
                .andExpect(status().isOk())
                .andExpect(jsonPath("result.update_status").value(true))
                .andExpect(jsonPath("result.message").value("Update user profile successfully"));
    }

    @Test
    @DisplayName("Test Update User Profile With Expired Token - Update Failed")
    @WithAnonymousUser
    void testUpdateUserProfile_ExpiredToken_Failed() throws Exception {
        var localDate = LocalDate.of(2025,12,31);
        var dob = Date.valueOf(localDate);
        UpdateRequest updateRequest = UpdateRequest.builder()
                .MSSV("12345678")
                .hovaten("Nguyễn Thanh Phong")
                .sdt("123456789")
                .diachi("test")
                .quequan("test")
                .gioitinh("test")
                .dob(dob)
                .build();
        UpdateRespond updateRespond = UpdateRespond.builder()
                .update_status(true)
                .message("Update user profile successfully")
                .build();

        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        String content = objectMapper.writeValueAsString(updateRequest);

        APIRespond<UpdateRespond> apiRespond = APIRespond.<UpdateRespond>builder()
                .result(updateRespond)
                .build();

        Mockito.when(userServices.UpdateProfile(any())).thenReturn(apiRespond.getResult());

        mockMvc.perform(put("/profile/my_profile/update")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(content))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("code").value(1006))
                .andExpect(jsonPath("message").value("Unauthenticated"));
    }

    //Test for GET /profile/all_profiles
    @Test
    @DisplayName("Test Get All User Profiles With Admin Role - Get Success")
    @WithMockUser(username = "admin@gmail.com" , roles = {"ADMIN"})
    void testGetAllUserProfiles_AdminRole_Success() throws Exception {
        var localDate = LocalDate.of(2025,12,31);
        var dob = Date.valueOf(localDate);

        GetProfileRespond getProfileRespond = GetProfileRespond.builder()
                .MSSV("12345678")
                .email("test@gmail.com")
                .hovaten("tester")
                .sdt("123456789")
                .diachi("test")
                .quequan("test")
                .gioitinh("test")
                .dob(dob)
                .build();

        List<GetProfileRespond> getProfileRespondList = Collections.singletonList(getProfileRespond);

        Mockito.when(adminServices.getAllUsersProfile()).thenReturn(getProfileRespondList);

        mockMvc.perform(get("/profile/all_profiles"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].mssv").value("12345678"))
                .andExpect(jsonPath("$[0].email").value("test@gmail.com"))
                .andExpect(jsonPath("$[0].hovaten").value("tester"))
                .andExpect(jsonPath("$[0].sdt").value("123456789"))
                .andExpect(jsonPath("$[0].diachi").value("test"))
                .andExpect(jsonPath("$[0].quequan").value("test"))
                .andExpect(jsonPath("$[0].gioitinh").value("test"))
                .andExpect(jsonPath("$[0].dob").value(dob.toString()));
    }

    @Test
    @DisplayName("Test Get All User Profiles With User Role - Forbidden Failed")
    @WithMockUser(username = "test@gmail.com")
    void testGetAllUserProfiles_UserRole_Failed() throws Exception {
        var localDate = LocalDate.of(2025,12,31);
        var dob = Date.valueOf(localDate);

        GetProfileRespond getProfileRespond = GetProfileRespond.builder()
                .MSSV("12345678")
                .email("test@gmail.com")
                .hovaten("tester")
                .sdt("123456789")
                .diachi("test")
                .quequan("test")
                .gioitinh("test")
                .dob(dob)
                .build();

        List<GetProfileRespond> getProfileRespondList = Collections.singletonList(getProfileRespond);

        Mockito.when(adminServices.getAllUsersProfile()).thenReturn(getProfileRespondList);

        mockMvc.perform(get("/profile/all_profiles"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("code").value(1007))
                .andExpect(jsonPath("message").value("You do not have permission"));
    }

    @Test
    @DisplayName("Test Get All User Profiles With Expired Token - Unauthenticated Failed")
    @WithAnonymousUser
    void testGetAllUserProfiles_ExpiredToken_Failed() throws Exception {
        var localDate = LocalDate.of(2025,12,31);
        var dob = Date.valueOf(localDate);

        GetProfileRespond getProfileRespond = GetProfileRespond.builder()
                .MSSV("12345678")
                .email("test@gmail.com")
                .hovaten("tester")
                .sdt("123456789")
                .diachi("test")
                .quequan("test")
                .gioitinh("test")
                .dob(dob)
                .build();

        List<GetProfileRespond> getProfileRespondList = Collections.singletonList(getProfileRespond);

        Mockito.when(adminServices.getAllUsersProfile()).thenReturn(getProfileRespondList);

        mockMvc.perform(get("/profile/all_profiles"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("code").value(1006))
                .andExpect(jsonPath("message").value("Unauthenticated"));
    }

    //Test for GET /profile/all_profiles/{MSSV}
    @Test
    @DisplayName("Test Search User Profiles With Admin Role - Search Success")
    @WithMockUser(username = "admin@gmail.com" , roles = {"ADMIN"})
    void SearchUserProfiles_AdminRole_Success() throws Exception {
        var localDate = LocalDate.of(2025,12,31);
        var dob = Date.valueOf(localDate);

        GetProfileRespond getProfileRespond = GetProfileRespond.builder()
                .MSSV("12345678")
                .email("test@gmail.com")
                .hovaten("tester")
                .sdt("123456789")
                .diachi("test")
                .quequan("test")
                .gioitinh("test")
                .dob(dob)
                .build();

        List<GetProfileRespond> getProfileRespondList = Collections.singletonList(getProfileRespond);

        Mockito.when(adminServices.SearchUserProfile(any())).thenReturn(getProfileRespondList);

        mockMvc.perform(get("/profile/all_profiles/12345678"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].mssv").value("12345678"))
                .andExpect(jsonPath("$[0].email").value("test@gmail.com"))
                .andExpect(jsonPath("$[0].hovaten").value("tester"))
                .andExpect(jsonPath("$[0].sdt").value("123456789"))
                .andExpect(jsonPath("$[0].diachi").value("test"))
                .andExpect(jsonPath("$[0].quequan").value("test"))
                .andExpect(jsonPath("$[0].gioitinh").value("test"))
                .andExpect(jsonPath("$[0].dob").value(dob.toString()));
    }

    @Test
    @DisplayName("Test Search User Profiles With Expired Token - Unauthenticated Failed")
    @WithAnonymousUser
    void SearchUserProfiles_ExpiredToken_Failed() throws Exception {
        var localDate = LocalDate.of(2025,12,31);
        var dob = Date.valueOf(localDate);

        GetProfileRespond getProfileRespond = GetProfileRespond.builder()
                .MSSV("12345678")
                .email("test@gmail.com")
                .hovaten("tester")
                .sdt("123456789")
                .diachi("test")
                .quequan("test")
                .gioitinh("test")
                .dob(dob)
                .build();

        List<GetProfileRespond> getProfileRespondList = Collections.singletonList(getProfileRespond);

        Mockito.when(adminServices.SearchUserProfile(any())).thenReturn(getProfileRespondList);

        mockMvc.perform(get("/profile/all_profiles/12345678"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("code").value(1006))
                .andExpect(jsonPath("message").value("Unauthenticated"));
    }

    //Test for DELETE /profile/all_profiles/{MSSV}
    @Test
    @DisplayName("Test Delete User Profiles With Admin Role - Delete Success")
    @WithMockUser(username = "admin@gmail.com" , roles = {"ADMIN"})
    void DeleteUserProfiles_AdminRole_Success() throws Exception {

        Mockito.doNothing().when(adminServices).deleteUserProfile(any());

        mockMvc.perform(delete("/profile/all_profiles/12345678"))
                .andExpect(status().isOk())
                .andExpect(content().string("User profile has been deleted"));
    }

    @Test
    @DisplayName("Test Delete User Profiles With Expired Token - Unauthenticated Failed")
    @WithAnonymousUser
    void DeleteUserProfiles_Expired_Token_Failed() throws Exception {

        Mockito.doNothing().when(adminServices).deleteUserProfile(any());

        mockMvc.perform(delete("/profile/all_profiles/12345678"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("code").value(1006))
                .andExpect(jsonPath("message").value("Unauthenticated"));
    }

}
