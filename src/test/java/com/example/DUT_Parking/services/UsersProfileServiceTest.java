package com.example.DUT_Parking.services;

import com.example.DUT_Parking.DTO.UpdateRequest;
import com.example.DUT_Parking.configuration.SecurityHolder;
import com.example.DUT_Parking.entity.UsersProfile;
import com.example.DUT_Parking.exception_handling.AppException;
import com.example.DUT_Parking.repository.UsersProfileRepo;
import com.example.DUT_Parking.respond.GetProfileRespond;
import com.example.DUT_Parking.services.impl.UpdateUserProfileImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.sql.Date;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

@ExtendWith(MockitoExtension.class)
public class UsersProfileServiceTest {
    @InjectMocks
    public UpdateUserProfileImpl userProfileService;

    @Mock
    private UsersProfileRepo usersProfileRepo;

    @Mock
    private SecurityHolder securityHolder;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;


    // Test for function UpdateProfile()
    @Test
    @DisplayName("Test function update profile have MSSV - Update success")
    void testUpdateProfile_WithMSSV_UpdateSuccess() {
        LocalDate dob = LocalDate.of(2020, 1, 1);
        Date dobDate = Date.valueOf(dob);

        UpdateRequest updateRequest = UpdateRequest.builder()
                .MSSV("MSSV")
                .sdt("test")
                .dob(dobDate)
                .diachi("diachi")
                .quequan("quequan")
                .gioitinh("gioitinh")
                .hovaten("hovaten")
                .build();

        UsersProfile usersProfile = UsersProfile.builder()
                .id(1L)
                .email("test@gmail.com")
                .build();

        String name = "test@gmail.com";

        Mockito.doReturn(securityContext).when(securityHolder).getContext();
        Mockito.doReturn(authentication).when(securityContext).getAuthentication();
        Mockito.doReturn(name).when(authentication).getName();
        Mockito.when(usersProfileRepo.findByEmail(name))
                .thenReturn(usersProfile);

        UpdateUserProfileImpl spy = Mockito.spy(userProfileService);
        var respond = spy.UpdateProfile(updateRequest);

        Assertions.assertTrue(respond.isUpdate_status());
        Assertions.assertEquals("Update user profile successfully" , respond.getMessage());

        Mockito.verify(usersProfileRepo).save(usersProfile);

    }

    @Test
    @DisplayName("Test function update profile without MSSV - Update success")
    void testUpdateProfile_WithoutMSSV_UpdateSuccess() {
        LocalDate dob = LocalDate.of(2020, 1, 1);
        Date dobDate = Date.valueOf(dob);

        UpdateRequest updateRequest = UpdateRequest.builder()
                .MSSV(null)
                .sdt("test")
                .dob(dobDate)
                .diachi("diachi")
                .quequan("quequan")
                .gioitinh("gioitinh")
                .hovaten("hovaten")
                .build();

        UsersProfile usersProfile = UsersProfile.builder()
                .id(1L)
                .email("test@gmail.com")
                .build();

        String name = "test@gmail.com";

        Mockito.doReturn(securityContext).when(securityHolder).getContext();
        Mockito.doReturn(authentication).when(securityContext).getAuthentication();
        Mockito.doReturn(name).when(authentication).getName();
        Mockito.when(usersProfileRepo.findByEmail(name))
                .thenReturn(usersProfile);

        UpdateUserProfileImpl spy = Mockito.spy(userProfileService);
        var respond = spy.UpdateProfile(updateRequest);

        Assertions.assertTrue(respond.isUpdate_status());
        Assertions.assertEquals("Update user profile successfully" , respond.getMessage());
        Assertions.assertEquals("GUEST" , usersProfile.getMSSV());

        Mockito.verify(usersProfileRepo).save(usersProfile);

    }

    // Test for function GetUserProfile()
    @Test
    @DisplayName("Test function GET my profile - Get success")
    void testGetMyProfile_GetSuccess() {
        LocalDate dob = LocalDate.of(2020, 1, 1);
        Date dobDate = Date.valueOf(dob);
        UsersProfile usersProfile = UsersProfile.builder()
                .id(1L)
                .hovaten("hovaten")
                .MSSV("MSSV")
                .sodu(9999)
                .email("test@gmail.com")
                .sdt("test")
                .gioitinh("gioitinh")
                .quequan("quequan")
                .diachi("diachi")
                .dob(dobDate)
                .build();

        String name = "test@gmail.com";

        GetProfileRespond getProfileRespond = GetProfileRespond.builder()
                .hovaten("hovaten")
                .MSSV("MSSV")
                .sodu(9999)
                .email("test@gmail.com")
                .sdt("test")
                .gioitinh("gioitinh")
                .quequan("quequan")
                .diachi("diachi")
                .dob(dobDate)
                .build();

        Mockito.doReturn(securityContext).when(securityHolder).getContext();
        Mockito.doReturn(authentication).when(securityContext).getAuthentication();
        Mockito.doReturn(name).when(authentication).getName();
        Mockito.when(usersProfileRepo.findByEmail(name))
                .thenReturn(usersProfile);

        UpdateUserProfileImpl spy = Mockito.spy(userProfileService);
        var respond = spy.GetUserProfile();

        Assertions.assertEquals(getProfileRespond, respond);
    }

    // Test for function SearchUserProfile()
    @Test
    @DisplayName("Test for search user profile function - Search success")
    void testSearchUserProfile_SearchSuccess() {
        LocalDate dob = LocalDate.of(2020, 1, 1);
        Date dobDate = Date.valueOf(dob);
        String mssv = "MSSV";
        UpdateUserProfileImpl spy = Mockito.spy(userProfileService);

        GetProfileRespond getProfileRespond = GetProfileRespond.builder()
                .hovaten("hovaten")
                .MSSV("MSSV")
                .sodu(9999)
                .email("test@gmail.com")
                .sdt("test")
                .gioitinh("gioitinh")
                .quequan("quequan")
                .diachi("diachi")
                .dob(dobDate)
                .build();

        UsersProfile usersProfile = UsersProfile.builder()
                .id(1L)
                .hovaten("hovaten")
                .MSSV("MSSV")
                .sodu(9999)
                .email("test@gmail.com")
                .sdt("test")
                .gioitinh("gioitinh")
                .quequan("quequan")
                .diachi("diachi")
                .dob(dobDate)
                .build();

        List<GetProfileRespond> getProfileRespondList = Collections.singletonList(getProfileRespond);
        List<UsersProfile> usersProfileList = Collections.singletonList(usersProfile);

        Mockito.when(usersProfileRepo.findByMSSV(mssv)).thenReturn(usersProfileList);

        var respond = spy.SearchUserProfile(mssv);

        Assertions.assertEquals(getProfileRespondList, respond);
    }

    @Test
    @DisplayName("Test for search user profile function - USER_NOT_EXISTED failed")
    void testSearchUserProfile_SearchFailed() {
        String mssv = "MSSV";
        UpdateUserProfileImpl spy = Mockito.spy(userProfileService);

        Mockito.when(usersProfileRepo.findByMSSV(mssv)).thenReturn(null);

        var exception = Assertions.assertThrows(AppException.class, () -> spy.SearchUserProfile(mssv));

        Assertions.assertEquals(1005, exception.getErrorCode().getCode());
        Assertions.assertEquals("User not existed" , exception.getMessage());
    }

    // Test for function deleteUserProfile()
    @Test
    @DisplayName("Test delete user profile function - Delete success")
    void testDeleteUserProfile_DeleteSuccess() {
        String mssv = "MSSV";

        UpdateUserProfileImpl spy = Mockito.spy(userProfileService);

        Mockito.doNothing().when(usersProfileRepo).deleteById(mssv);

        spy.deleteUserProfile(mssv);

        Mockito.verify(usersProfileRepo).deleteById(mssv);
    }

    @Test
    @DisplayName("Test delete user profile function - USER_NOT_EXISTED failed")
    void testDeleteUserProfile_DeleteFailed() {
        String mssv = "MSSV";

        UpdateUserProfileImpl spy = Mockito.spy(userProfileService);

        Mockito.doThrow(EmptyResultDataAccessException.class).when(usersProfileRepo).deleteById(mssv);

        var exception = Assertions.assertThrows(AppException.class , () -> spy.deleteUserProfile(mssv));

        Assertions.assertEquals(1005, exception.getErrorCode().getCode());
        Assertions.assertEquals("User not existed" , exception.getMessage());
    }

    // Test for function GetAllUsersProfile()
    @Test
    @DisplayName("Test get all user profiles function - Get success")
    void testGetAllUserProfiles_GetSuccess() {
        LocalDate dob = LocalDate.of(2020, 1, 1);
        Date dobDate = Date.valueOf(dob);
        UpdateUserProfileImpl spy = Mockito.spy(userProfileService);

        GetProfileRespond getProfileRespond = GetProfileRespond.builder()
                .hovaten("hovaten")
                .MSSV("MSSV")
                .sodu(9999)
                .email("test@gmail.com")
                .sdt("test")
                .gioitinh("gioitinh")
                .quequan("quequan")
                .diachi("diachi")
                .dob(dobDate)
                .build();

        UsersProfile usersProfile = UsersProfile.builder()
                .id(1L)
                .hovaten("hovaten")
                .MSSV("MSSV")
                .sodu(9999)
                .email("test@gmail.com")
                .sdt("test")
                .gioitinh("gioitinh")
                .quequan("quequan")
                .diachi("diachi")
                .dob(dobDate)
                .build();

        List<GetProfileRespond> getProfileRespondList = Collections.singletonList(getProfileRespond);
        List<UsersProfile> usersProfileList = Collections.singletonList(usersProfile);

        Mockito.when(usersProfileRepo.findAll()).thenReturn(usersProfileList);

        var respond = spy.getAllUsersProfile();

        Assertions.assertEquals(getProfileRespondList, respond);
    }
}
