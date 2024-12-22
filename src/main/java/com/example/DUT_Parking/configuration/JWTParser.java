package com.example.DUT_Parking.configuration;

import com.nimbusds.jwt.SignedJWT;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.text.ParseException;

@Component
public class JWTParser {
    public SignedJWT parse(String token) throws ParseException {
        return SignedJWT.parse(token);
    }
}
