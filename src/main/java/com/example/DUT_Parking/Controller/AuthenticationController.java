package com.example.DUT_Parking.Controller;

import com.example.DUT_Parking.DTO.AuthenticationRequest;
import com.example.DUT_Parking.DTO.IntrospectLoginToken;
import com.example.DUT_Parking.DTO.LogoutRequest;
import com.example.DUT_Parking.respond.*;
import com.example.DUT_Parking.services.AuthenticationService;
import com.nimbusds.jose.JOSEException;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PreAuthorize;
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
    APIRespond <AuthenticationRespond> authenticated(@RequestBody @Valid AuthenticationRequest request) throws JOSEException {
        var result = authenticationService.authenticated(request);
        return APIRespond.<AuthenticationRespond>builder()
                .result(result)
                .build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping ("/login-users")
    List<GetLoginUsers> getAllUsers() {
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

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping ("/logout-users")
    List<GetLogoutUsers> getAllLogoutUsers() {
        return authenticationService.getAllLogoutUsers();
    };
}

