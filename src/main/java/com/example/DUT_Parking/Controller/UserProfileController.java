package com.example.DUT_Parking.Controller;

import com.example.DUT_Parking.DTO.UpdateRequest;
import com.example.DUT_Parking.entity.UsersProfile;
import com.example.DUT_Parking.respond.APIRespond;
import com.example.DUT_Parking.respond.GetProfileRespond;
import com.example.DUT_Parking.respond.UpdateRespond;
import com.example.DUT_Parking.services.AdminServices;
import com.example.DUT_Parking.services.UserServices;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/profile")
@FieldDefaults(level = AccessLevel.PRIVATE , makeFinal = true)
public class UserProfileController {
    private  final UserServices userServices;
    private final AdminServices adminServices;

    public UserProfileController(@Qualifier("updateUserProfileImpl") UserServices userServices, @Qualifier("updateUserProfileImpl") AdminServices adminServices) {
        this.userServices = userServices;
        this.adminServices = adminServices;
    }

    @GetMapping("/my_profile")
    //Service for user only to get their profile
    APIRespond <GetProfileRespond> GetUserProfile() {
        return APIRespond.<GetProfileRespond>builder()
                .result(userServices.GetUserProfile())
                .build();
    }

    @PutMapping("/my_profile/update")
    APIRespond <UpdateRespond> UpdateProfile(@RequestBody @Valid UpdateRequest request) {
        return APIRespond.<UpdateRespond>builder()
                .result(userServices.UpdateProfile(request))
                .build();
    }

    @GetMapping("/all_profiles")
    List<GetProfileRespond> getAllUserProfile() {
        return adminServices.getAllUsersProfile();
    }

    @GetMapping("/all_profiles/{MSSV}")
    //Service for ADMIN to get the user profile they want to get by ho_va_ten
    APIRespond <List<GetProfileRespond>> SearchUserProfile(@PathVariable String MSSV) {
        return APIRespond.<List<GetProfileRespond>>builder()
                .result(adminServices.SearchUserProfile(MSSV))
                .build();
    }

    @DeleteMapping("/all_profiles/{MSSV}")
    String DeleteUserProfile(@PathVariable String MSSV) {
        adminServices.deleteUserProfile(MSSV);
        return "User profile has been deleted";
    }


}
