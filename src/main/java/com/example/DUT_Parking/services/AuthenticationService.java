package com.example.DUT_Parking.services;

import com.example.DUT_Parking.DTO.AuthenticationRequest;
import com.example.DUT_Parking.DTO.ForgetPasswordRequest;
import com.example.DUT_Parking.DTO.IntrospectLoginToken;
import com.example.DUT_Parking.DTO.LogoutRequest;
import com.example.DUT_Parking.entity.UsersProfile;
import com.example.DUT_Parking.respond.AuthenticationRespond;
import com.example.DUT_Parking.respond.GetLoginUsers;
import com.example.DUT_Parking.respond.GetLogoutUsers;
import com.example.DUT_Parking.respond.IntrospectRespond;
import com.nimbusds.jose.JOSEException;
import jakarta.mail.MessagingException;

import java.text.ParseException;
import java.util.List;

public interface AuthenticationService {
    AuthenticationRespond authenticated(AuthenticationRequest authenticationRequest) throws JOSEException;
    IntrospectRespond introspect(IntrospectLoginToken token) throws JOSEException, ParseException;
    List<GetLoginUsers> getAllUsers();
    void logout(LogoutRequest token) throws ParseException, JOSEException;
    List<GetLogoutUsers> getAllLogoutUsers();
}
