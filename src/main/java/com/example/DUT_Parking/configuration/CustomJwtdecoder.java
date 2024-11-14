package com.example.DUT_Parking.configuration;

import com.example.DUT_Parking.DTO.IntrospectLoginToken;
import com.example.DUT_Parking.exception_handling.AppException;
import com.example.DUT_Parking.exception_handling.ErrorCode;
import com.example.DUT_Parking.services.AuthenticationService;
import com.nimbusds.jose.JOSEException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.stereotype.Component;

import javax.crypto.spec.SecretKeySpec;
import java.text.ParseException;
import java.util.Objects;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Component
public class CustomJwtdecoder implements JwtDecoder {
    @NonFinal
    protected static final String signer_key = "4B8SWV0opYWRgxeKoKost+CvEfqKhCPV0G1SFgU6V1vLOLbBWo5hE1JhpQUV7gWL";

    @Autowired
    AuthenticationService authenticationService;
    @NonFinal
    NimbusJwtDecoder nimbusJwtDecoder = null;

    @Override
    public Jwt decode(String token) throws JwtException {

        try {
            var response = authenticationService.introspect(IntrospectLoginToken.builder()
                            .token(token)
                    .build());
            if (!(response.isValid() | response.isValid_expired())){
                throw new AppException(ErrorCode.UNAUTHENTICATED);
            }
        } catch (ParseException | JOSEException e) {
            throw new JwtException(e.getMessage());
        }

        if (Objects.isNull(nimbusJwtDecoder)) {
            SecretKeySpec key = new SecretKeySpec(signer_key.getBytes(), "HS512");
            nimbusJwtDecoder= NimbusJwtDecoder
                    .withSecretKey(key)
                    .macAlgorithm(MacAlgorithm.HS512)
                    .build();
        }
        return nimbusJwtDecoder.decode(token);
    }
}
