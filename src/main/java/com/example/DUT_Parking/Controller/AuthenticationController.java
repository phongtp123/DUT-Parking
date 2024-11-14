package com.example.DUT_Parking.Controller;

import com.example.DUT_Parking.DTO.AuthenticationRequest;
import com.example.DUT_Parking.DTO.IntrospectLoginToken;
import com.example.DUT_Parking.DTO.LogoutRequest;
import com.example.DUT_Parking.entity.LoginUsers;
import com.example.DUT_Parking.respond.APIRespond;
import com.example.DUT_Parking.respond.AuthenticationRespond;
import com.example.DUT_Parking.respond.IntrospectRespond;
import com.example.DUT_Parking.services.AuthenticationService;
import com.nimbusds.jose.JOSEException;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.util.List;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationController {
    AuthenticationService authenticationService;

    @PostMapping ("/login")
    APIRespond <AuthenticationRespond> authenticated(@RequestBody AuthenticationRequest request) {
        var result = authenticationService.authenticated(request);
        return APIRespond.<AuthenticationRespond>builder()
                .result(result)
                .build();
    }

    @GetMapping ("/login-users")
    List<LoginUsers> getAllUsers() {
        return authenticationService.getAllUsers();
    }

    @PostMapping ("/introspect")
    APIRespond <IntrospectRespond> introspect(@RequestBody IntrospectLoginToken token) throws ParseException, JOSEException {
        var result = authenticationService.introspect(token);
        return APIRespond.<IntrospectRespond>builder()
                .result(result)
                .build();
    }

    @PostMapping ("/logout")
    APIRespond <Void> logout(@RequestBody LogoutRequest token) throws ParseException, JOSEException {
        authenticationService.logout(token);
        return APIRespond.<Void>builder()
                .build();
    }
}

