package com.example.DUT_Parking.services.impl;

import com.example.DUT_Parking.DTO.AuthenticationRequest;
import com.example.DUT_Parking.DTO.ForgetPasswordRequest;
import com.example.DUT_Parking.DTO.IntrospectLoginToken;
import com.example.DUT_Parking.DTO.LogoutRequest;
import com.example.DUT_Parking.entity.LogoutUsers;
import com.example.DUT_Parking.entity.LoginUsers;
import com.example.DUT_Parking.entity.RegisteredUsers;
import com.example.DUT_Parking.entity.UsersProfile;
import com.example.DUT_Parking.exception_handling.AppException;
import com.example.DUT_Parking.exception_handling.ErrorCode;
import com.example.DUT_Parking.repository.LogoutUserRepo;
import com.example.DUT_Parking.repository.LoginUserRepo;
import com.example.DUT_Parking.repository.RegisteredUserRepo;
import com.example.DUT_Parking.repository.UsersProfileRepo;
import com.example.DUT_Parking.respond.*;
import com.example.DUT_Parking.services.AuthenticationService;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.JWTParser;
import com.nimbusds.jwt.SignedJWT;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.StringJoiner;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationImpl implements AuthenticationService {
        UsersProfileRepo usersProfileRepo;
        RegisteredUserRepo registeredUserRepo;
        LoginUserRepo loginUserRepo;
        LogoutUserRepo logoutUserRepo;
        com.example.DUT_Parking.configuration.JWTParser jwtParser;
//        JavaMailSender mailSender;

        @NonFinal
        public static final String signer_key = "4B8SWV0opYWRgxeKoKost+CvEfqKhCPV0G1SFgU6V1vLOLbBWo5hE1JhpQUV7gWL";

        public AuthenticationRespond authenticated(AuthenticationRequest request) throws JOSEException {
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
                                var loginUsersList = loginUserRepo.findByRegisteredUsers(registeredUser);
                                if (loginUsersList == null) {
                                        loginUserRepo.save(loginUser);
                                }
                                return AuthenticationRespond.builder()
                                        .token(token)
                                        .authenticated(true)
                                        .build();
                        }
                        else {
                                var loginUsersList = loginUserRepo.findByRegisteredUsers(registeredUser);
                                if (loginUsersList != null)
                                {
                                        loginUserRepo.delete(loginUsersList);
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

        public String generateToken(UsersProfile usersProfile) throws JOSEException{
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
                        throw new AppException(ErrorCode.JOSEE_EXCEPTION);
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

        public SignedJWT verify_token(String token) throws ParseException, JOSEException , AppException {
                JWSVerifier verifier = new MACVerifier(signer_key.getBytes());
                SignedJWT signedJWT = jwtParser.parse(token);
                Date expirationDate = signedJWT.getJWTClaimsSet().getExpirationTime();
                var registeredUser = registeredUserRepo.findByEmail(signedJWT.getJWTClaimsSet().getSubject());
                var validation = signedJWT.verify(verifier);
                if (!validation){
                        throw new AppException(ErrorCode.UNAUTHENTICATED);
                }
                if (!expirationDate.after(new Date())) {
                        throw new AppException(ErrorCode.UNAUTHENTICATED);
                }
                if (logoutUserRepo.existsByRegisteredUsers(registeredUser)) {
                        throw new AppException(ErrorCode.UNAUTHENTICATED);
                }
                return signedJWT;
        }

        public  List<GetLoginUsers> getAllUsers() {

                List<LoginUsers> loginUsers = loginUserRepo.findAll();

                return loginUsers.stream().map(loginUser -> {
                        GetLoginUsers getLoginUsers = new GetLoginUsers();
                        getLoginUsers.setId(loginUser.getId());
                        getLoginUsers.setEmail(loginUser.getEmail());
                        return getLoginUsers;
                }).collect(Collectors.toList());
        }

        public List<GetLogoutUsers> getAllLogoutUsers() {
                List<LogoutUsers> logoutUsers = logoutUserRepo.findAll();

                return logoutUsers.stream().map(logoutUser -> {
                        GetLogoutUsers getLogoutUsers = new GetLogoutUsers();
                        getLogoutUsers.setId(logoutUser.getId());
                        getLogoutUsers.setEmail(logoutUser.getEmail());
                        getLogoutUsers.setExpiryDate(logoutUser.getExpiryDate());
                        return getLogoutUsers;
                }).collect(Collectors.toList());
        }

//        public void sendPasswordEmail(String email) throws MessagingException {
//                try {
//                        MimeMessage mimeMessage = mailSender.createMimeMessage();
//                        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true);
//                        mimeMessageHelper.setFrom("phongboy1709@gmail.com" , "");
//                        mimeMessageHelper.setTo(email);
//                        mimeMessageHelper.setSubject("Reset Password Request");
//
//                        String resetLink = String.format("http://localhost:8080/auth/reset-password?email=%s", URLEncoder.encode(email, StandardCharsets.UTF_8));
//                        String emailContent = String.format("""
//                <div>
//                    <p>Hi,</p>
//                    <p>You requested a password reset. Click the link below to reset your password:</p>
//                    <a href="%s" target="_blank">Reset Password</a>
//                    <p>If you did not request this, please ignore this email.</p>
//                </div>
//                """, resetLink);
//
//                        mimeMessageHelper.setText(emailContent, true);
//                        mailSender.send(mimeMessage);
//                } catch (MessagingException e) {
//                        throw new AppException(ErrorCode.EMAIL_SENDING_FAILED);
//                }
//        }
//
//        private boolean isValidEmail(String email) {
//                String emailRegex = "^[a-zA-Z0-9]+@gmail\\.com$";
//                return email != null && email.matches(emailRegex);
//        }
//
//        public String forgetPassword(ForgetPasswordRequest request) {
//                if (!isValidEmail(request.getEmail())) {
//                        throw new AppException(ErrorCode.EMAIL_INVALID);
//                }
//
//                var registeredUser = registeredUserRepo.findByEmail(request.getEmail().trim());
//                if (registeredUser == null) {
//                        throw new AppException(ErrorCode.USER_NOT_EXISTED);
//                }
//
//                try {
//                        sendPasswordEmail(request.getEmail().trim());
//                } catch (MessagingException e) {
//                        throw new AppException(ErrorCode.EMAIL_SENDING_FAILED);
//                }
//
//                return String.format("The request to reset your password has been sent to %s. Please check your email to set your new password.", request.getEmail().trim());
//        }
//
//        public String resetPassword(String email , String newPassword) {
//                var registeredUser = registeredUserRepo.findByEmail(email);
//                if (registeredUser == null) {
//                        throw new AppException(ErrorCode.USER_NOT_EXISTED);
//                }
//                registeredUser.setPassword(newPassword);
//                registeredUserRepo.save(registeredUser);
//                return "Reset password successful";
//        }

}
