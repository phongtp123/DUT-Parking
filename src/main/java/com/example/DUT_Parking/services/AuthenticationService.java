package com.example.DUT_Parking.services;

import com.example.DUT_Parking.DTO.AuthenticationRequest;
import com.example.DUT_Parking.DTO.IntrospectLoginToken;
import com.example.DUT_Parking.DTO.LogoutRequest;
import com.example.DUT_Parking.entity.LoginUsers;
import com.example.DUT_Parking.entity.LogoutUsers;
import com.example.DUT_Parking.respond.AuthenticationRespond;
import com.example.DUT_Parking.respond.IntrospectRespond;
import com.nimbusds.jose.JOSEException;
import org.springframework.security.access.prepost.PreAuthorize;

import java.text.ParseException;
import java.util.List;

public interface AuthenticationService {
    AuthenticationRespond authenticated(AuthenticationRequest authenticationRequest);
    IntrospectRespond introspect(IntrospectLoginToken token) throws JOSEException, ParseException;
    @PreAuthorize("hasRole('ADMIN')")
    List<LoginUsers> getAllUsers();
    void logout(LogoutRequest token) throws ParseException, JOSEException;
    @PreAuthorize("hasRole('ADMIN')")
    List<LogoutUsers> getAllLogoutUsers();
}
