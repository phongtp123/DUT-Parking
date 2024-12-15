package com.example.DUT_Parking.Controller;

import com.example.DUT_Parking.respond.APIRespond;
import com.example.DUT_Parking.DTO.RegisterRequest;
import com.example.DUT_Parking.entity.RegisteredUsers;
import com.example.DUT_Parking.respond.GetRegisteredUsers;
import com.example.DUT_Parking.respond.RegisterRespond;
import com.example.DUT_Parking.services.RegisterService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/auth")
@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE , makeFinal = true)
public class RegisterController {

    RegisterService registerService;

    @PostMapping("/register")
    APIRespond <RegisterRespond> register(@RequestBody @Valid RegisterRequest register_info) {
        boolean result = registerService.register(register_info);
        return APIRespond.<RegisterRespond>builder()
                .result(RegisterRespond.builder()
                        .register_status(result)
                        .build())
                .build();
    }

    @GetMapping("/registered_users/{email}")
    APIRespond <GetRegisteredUsers> search(@PathVariable("email") String email) {
        GetRegisteredUsers result = registerService.search(email);
        return APIRespond.<GetRegisteredUsers>builder()
                .result(result)
                .build();
    }

    @GetMapping("/registered_users")
    List<GetRegisteredUsers> getAllUsers() {
        return registerService.getAllUsers();
    }

    @DeleteMapping("/registered_users/{id}")
    String deleteUser(@PathVariable("id") int id) {
        registerService.delete(id);
        return "User has been deleted";
    }
}
