package com.example.DUT_Parking.services.impl;

import com.example.DUT_Parking.DTO.AuthenticationRequest;
import com.example.DUT_Parking.DTO.IntrospectLoginToken;
import com.example.DUT_Parking.DTO.LogoutRequest;
import com.example.DUT_Parking.entity.LogoutUsers;
import com.example.DUT_Parking.entity.LoginUsers;
import com.example.DUT_Parking.entity.UsersProfile;
import com.example.DUT_Parking.exception_handling.AppException;
import com.example.DUT_Parking.exception_handling.ErrorCode;
import com.example.DUT_Parking.repository.LogoutUserRepo;
import com.example.DUT_Parking.repository.LoginUserRepo;
import com.example.DUT_Parking.repository.RegisteredUserRepo;
import com.example.DUT_Parking.repository.UsersProfileRepo;
import com.example.DUT_Parking.respond.AuthenticationRespond;
import com.example.DUT_Parking.respond.IntrospectRespond;
import com.example.DUT_Parking.services.AuthenticationService;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.StringJoiner;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationImpl implements AuthenticationService {
        UsersProfileRepo usersProfileRepo;
        RegisteredUserRepo registeredUserRepo;
        LoginUserRepo loginUserRepo;
        LogoutUserRepo logoutUserRepo;

        @NonFinal
        protected static final String signer_key = "4B8SWV0opYWRgxeKoKost+CvEfqKhCPV0G1SFgU6V1vLOLbBWo5hE1JhpQUV7gWL";

        public AuthenticationRespond authenticated(AuthenticationRequest request) {
                var registeredUser = registeredUserRepo.findByEmail(request.getEmail());
                var user = usersProfileRepo.findByEmail(request.getEmail());
                if (registeredUser == null) {
                        throw new AppException(ErrorCode.USER_NOT_EXISTED);
                }
                else {
                        String password = request.getPassword();
                        String registered_password = registeredUser.getPassword();
                        boolean authenticate = password.equals(registered_password);
                        if (authenticate) {
                                var token = generateToken(user);
                                LoginUsers loginUser = new LoginUsers();
                                loginUser.setRegisteredUsers(registeredUser);
                                loginUser.setEmail(registeredUser.getEmail());
                                if (loginUserRepo.findByRegisteredUsers(registeredUser) != null) {
                                        return AuthenticationRespond.builder()
                                                .token(token)
                                                .authenticated(true)
                                                .build();
                                }
                                else {
                                        loginUserRepo.save(loginUser);
                                        return AuthenticationRespond.builder()
                                                .token(token)
                                                .authenticated(true)
                                                .build();
                                }
                        }
                        else {
                                if (loginUserRepo.findByRegisteredUsers(registeredUser) != null){
                                        loginUserRepo.delete(loginUserRepo.findByRegisteredUsers(registeredUser));
                                }
                                throw new AppException(ErrorCode.UNAUTHENTICATED);
                        }

                }
        }

        public void logout(LogoutRequest token) throws ParseException, JOSEException {
                var logoutToken = verify_token(token.getToken());
                String object = logoutToken.getJWTClaimsSet().getSubject();
                var registeredUser = registeredUserRepo.findByEmail(object);
                Date expiryDate = logoutToken.getJWTClaimsSet().getExpirationTime();
                LogoutUsers logoutUsers = LogoutUsers.builder()
                        .registeredUsers(registeredUser)
                        .email(registeredUser.getEmail())
                        .expiryDate(expiryDate)
                        .build();
                var ghostToken = loginUserRepo.findByRegisteredUsers(registeredUser);
                loginUserRepo.delete(ghostToken);
                logoutUserRepo.save(logoutUsers);
        }

        private String generateToken(UsersProfile usersProfile) {
                //Header voi thuat toan JWS HS512
                JWSHeader header = new JWSHeader(JWSAlgorithm.HS512);
                //Claimset va Payload
                JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
                        .subject(usersProfile.getEmail())
                        .issuer("example.com")
                        .issueTime(new Date())
                        .expirationTime(new Date(Instant.now().plus(1 , ChronoUnit.DAYS).toEpochMilli()))
                        .jwtID(UUID.randomUUID().toString())
                        .claim("scope" , buildScope(usersProfile))
                        .build();
                Payload payload = new Payload(jwtClaimsSet.toJSONObject());
                //Token la mot object gom 2 tham so header va Payload
                JWSObject jwsObject = new JWSObject(header, payload);

                try {
                        //Signature trong do signer_key la mot ma 32bit bat ki
                        jwsObject.sign(new MACSigner(signer_key.getBytes()));
                        return jwsObject.serialize();
                } catch (JOSEException e) {
                        log.error("Cannot create token", e);
                        throw new RuntimeException(e);
                }
        }

        private String buildScope(UsersProfile usersProfile) {
                StringJoiner scope = new StringJoiner(" ");
                if (!CollectionUtils.isEmpty(usersProfile.getRoles())) {
                        usersProfile.getRoles().forEach(scope::add);
                }
                return scope.toString();
        }

        public IntrospectRespond introspect(IntrospectLoginToken token) throws JOSEException, ParseException {
                var introspect_token = token.getToken();
                try {
                        verify_token(introspect_token);
                } catch (AppException e) {
                        return IntrospectRespond.builder()
                                .valid(false)
                                .valid_expired(false)
                                .build();
                }
                return IntrospectRespond.builder()
                        .valid(true)
                        .valid_expired(true)
                        .build();

        }

        private SignedJWT verify_token(String token) throws ParseException, JOSEException {
                JWSVerifier verifier = new MACVerifier(signer_key.getBytes());
                SignedJWT signedJWT = SignedJWT.parse(token);
                Date expirationDate = signedJWT.getJWTClaimsSet().getExpirationTime();
                var registeredUser = registeredUserRepo.findByEmail(signedJWT.getJWTClaimsSet().getSubject());
                var validation = signedJWT.verify(verifier);
                if (!(validation && expirationDate.after(new Date()))){
                        throw new AppException(ErrorCode.UNAUTHENTICATED);
                }
                if (logoutUserRepo.existsByRegisteredUsers(registeredUser)) {
                        throw new AppException(ErrorCode.UNAUTHENTICATED);
                }
                return signedJWT;
        }

        public List<LoginUsers> getAllUsers() {
                return loginUserRepo.findAll();
        }

        public List<LogoutUsers> getAllLogoutUsers() {
                return logoutUserRepo.findAll();
        }

}
