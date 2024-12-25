package com.example.DUT_Parking.controller;

import com.example.DUT_Parking.DTO.UpdateRequest;
import com.example.DUT_Parking.respond.*;
import com.example.DUT_Parking.services.AdminServices;
import com.example.DUT_Parking.services.UserServices;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.qameta.allure.Feature;
import io.qameta.allure.Link;
import io.qameta.allure.TmsLink;
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
@DisplayName("User Profile Controller Test")
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
    @Feature("Get My Profile")
    @Test
    @TmsLink("28")
    @DisplayName("Test Get User Profile With Valid Token - Get Success")
    @WithMockUser(username = "test@gmail.com")
    @Link(name = "PROFILE-01" , type = "task")
    void testGetUserProfile_ValidToken_Success() throws Exception {
        var localDate = LocalDate.now().minusYears(20);
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

    @Feature("Get My Profile")
    @Test
    @TmsLink("29")
    @DisplayName("Test Get User Profile With Expired Token - Get Failed")
    @WithAnonymousUser
    @Link(name = "PROFILE-02" , type = "task")
    void testGetUserProfile_ExpiredToken_Failed() throws Exception {
        var localDate = LocalDate.now().minusYears(20);
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
    @Feature("Update Profile")
    @Test
    @TmsLink("30")
    @DisplayName("Test Update User Profile With Valid Token - Update Success")
    @WithMockUser(username = "test@gmail.com")
    @Link(name = "PROFILE-03" , type = "task")
    void testUpdateUserProfile_ValidToken_Success() throws Exception {
        var localDate = LocalDate.now().minusYears(20);
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

    @Feature("Update Profile")
    @Test
    @TmsLink("31")
    @DisplayName("Test Update User Profile With Expired Token - Update Failed")
    @WithAnonymousUser
    @Link(name = "PROFILE-04" , type = "task")
    void testUpdateUserProfile_ExpiredToken_Failed() throws Exception {
        var localDate = LocalDate.now().minusYears(20);
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

    @Feature("Update Profile")
    @Test
    @TmsLink("32")
    @DisplayName("Test Update User Profile Null or Blank Name - Update Failed")
    @WithMockUser(username = "test@gmail.com")
    @Link(name = "PROFILE-05" , type = "task")
    void testUpdateUserProfile_InvalidName_Failed() throws Exception {
        var localDate = LocalDate.now().minusYears(20);
        var dob = Date.valueOf(localDate);
        UpdateRequest updateRequest = UpdateRequest.builder()
                .MSSV("12345678")
                .hovaten(null)
                .sdt("123456789")
                .diachi("test")
                .quequan("test")
                .gioitinh("test")
                .dob(dob)
                .build();

        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        String content = objectMapper.writeValueAsString(updateRequest);

        mockMvc.perform(put("/profile/my_profile/update")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(content))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("code").value(1009))
                .andExpect(jsonPath("message").value("Họ và tên không được bỏ trống"));
    }

    @Feature("Update Profile")
    @Test
    @TmsLink("33")
    @DisplayName("Test Update User Profile Null or Blank Phone Number - Update Failed")
    @WithMockUser(username = "test@gmail.com")
    @Link(name = "PROFILE-06" , type = "task")
    void testUpdateUserProfile_InvalidSDT_Failed() throws Exception {
        var localDate = LocalDate.now().minusYears(20);
        var dob = Date.valueOf(localDate);
        UpdateRequest updateRequest = UpdateRequest.builder()
                .MSSV("12345678")
                .hovaten("Nguyễn Thanh Phong")
                .sdt(null)
                .diachi("test")
                .quequan("test")
                .gioitinh("test")
                .dob(dob)
                .build();

        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        String content = objectMapper.writeValueAsString(updateRequest);

        mockMvc.perform(put("/profile/my_profile/update")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(content))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("code").value(1013))
                .andExpect(jsonPath("message").value("Số điện thoại không được bỏ trống"));
    }

    @Feature("Update Profile")
    @Test
    @TmsLink("34")
    @DisplayName("Test Update User Profile Null or Blank Address - Update Failed")
    @WithMockUser(username = "test@gmail.com")
    @Link(name = "PROFILE-07" , type = "task")
    void testUpdateUserProfile_InvalidAddress_Failed() throws Exception {
        var localDate = LocalDate.now().minusYears(20);
        var dob = Date.valueOf(localDate);
        UpdateRequest updateRequest = UpdateRequest.builder()
                .MSSV("12345678")
                .hovaten("Nguyễn Thanh Phong")
                .sdt("123456789")
                .diachi(null)
                .quequan("test")
                .gioitinh("test")
                .dob(dob)
                .build();

        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        String content = objectMapper.writeValueAsString(updateRequest);

        mockMvc.perform(put("/profile/my_profile/update")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(content))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("code").value(1011))
                .andExpect(jsonPath("message").value("Địa chỉ không được bỏ trống"));
    }

    @Feature("Update Profile")
    @Test
    @TmsLink("35")
    @DisplayName("Test Update User Profile Null or Blank Hometown - Update Failed")
    @WithMockUser(username = "test@gmail.com")
    @Link(name = "PROFILE-08" , type = "task")
    void testUpdateUserProfile_InvalidHometown_Failed() throws Exception {
        var localDate = LocalDate.now().minusYears(20);
        var dob = Date.valueOf(localDate);
        UpdateRequest updateRequest = UpdateRequest.builder()
                .MSSV("12345678")
                .hovaten("Nguyễn Thanh Phong")
                .sdt("123456789")
                .diachi("test")
                .quequan(null)
                .gioitinh("test")
                .dob(dob)
                .build();

        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        String content = objectMapper.writeValueAsString(updateRequest);

        mockMvc.perform(put("/profile/my_profile/update")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(content))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("code").value(1012))
                .andExpect(jsonPath("message").value("Quê quán không được bỏ trống"));
    }

    @Feature("Update Profile")
    @Test
    @TmsLink("36")
    @DisplayName("Test Update User Profile Null or Blank Gender - Update Failed")
    @WithMockUser(username = "test@gmail.com")
    @Link(name = "PROFILE-09" , type = "task")
    void testUpdateUserProfile_InvalidGender_Failed() throws Exception {
        var localDate = LocalDate.now().minusYears(20);
        var dob = Date.valueOf(localDate);
        UpdateRequest updateRequest = UpdateRequest.builder()
                .MSSV("12345678")
                .hovaten("Nguyễn Thanh Phong")
                .sdt("123456789")
                .diachi("test")
                .quequan("test")
                .gioitinh(null)
                .dob(dob)
                .build();

        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        String content = objectMapper.writeValueAsString(updateRequest);

        mockMvc.perform(put("/profile/my_profile/update")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(content))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("code").value(1010))
                .andExpect(jsonPath("message").value("Giới tính không được bỏ trống"));
    }

    @Feature("Update Profile")
    @Test
    @TmsLink("37")
    @DisplayName("Test Update User Profile Under 18 Dob - Update Failed")
    @WithMockUser(username = "test@gmail.com")
    @Link(name = "PROFILE-10" , type = "task")
    void testUpdateUserProfile_InvalidDob_Failed() throws Exception {
        var localDate = LocalDate.now().minusYears(1);
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

        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        String content = objectMapper.writeValueAsString(updateRequest);

        mockMvc.perform(put("/profile/my_profile/update")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(content))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("code").value(1008))
                .andExpect(jsonPath("message").value("Your age must be at least 18"));
    }

    //Test for GET /profile/all_profiles
    @Feature("Get All Profile")
    @Test
    @TmsLink("38")
    @DisplayName("Test Get All User Profiles With Admin Role - Get Success")
    @WithMockUser(username = "admin@gmail.com" , roles = {"ADMIN"})
    @Link(name = "PROFILE-11" , type = "task")
    void testGetAllUserProfiles_AdminRole_Success() throws Exception {
        var localDate = LocalDate.now().minusYears(20);
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

    @Feature("Get All Profile")
    @Test
    @TmsLink("39")
    @DisplayName("Test Get All User Profiles With User Role - Forbidden Failed")
    @WithMockUser(username = "test@gmail.com")
    @Link(name = "PROFILE-12" , type = "task")
    void testGetAllUserProfiles_UserRole_Failed() throws Exception {
        var localDate = LocalDate.now().minusYears(20);
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

    @Feature("Get All Profile")
    @Test
    @TmsLink("40")
    @DisplayName("Test Get All User Profiles With Expired Token - Unauthenticated Failed")
    @WithAnonymousUser
    @Link(name = "PROFILE-13" , type = "task")
    void testGetAllUserProfiles_ExpiredToken_Failed() throws Exception {
        var localDate = LocalDate.now().minusYears(20);
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
    @Feature("Search User Profile By MSSV")
    @Test
    @TmsLink("41")
    @DisplayName("Test Search User Profiles With Admin Role - Search Success")
    @WithMockUser(username = "admin@gmail.com" , roles = {"ADMIN"})
    @Link(name = "PROFILE-14" , type = "task")
    void SearchUserProfiles_AdminRole_Success() throws Exception {
        var localDate = LocalDate.now().minusYears(20);
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

    @Feature("Search User Profile By MSSV")
    @Test
    @TmsLink("42")
    @DisplayName("Test Search User Profiles With Expired Token - Unauthenticated Failed")
    @WithAnonymousUser
    @Link(name = "PROFILE-15" , type = "task")
    void SearchUserProfiles_ExpiredToken_Failed() throws Exception {
        var localDate = LocalDate.now().minusYears(20);
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
}
