package com.example.DUT_Parking.services;

import com.example.DUT_Parking.DTO.AuthenticationRequest;
import com.example.DUT_Parking.DTO.IntrospectLoginToken;
import com.example.DUT_Parking.DTO.LogoutRequest;
import com.example.DUT_Parking.DTO.RegisterRequest;
import com.example.DUT_Parking.configuration.JWTParser;
import com.example.DUT_Parking.entity.LoginUsers;
import com.example.DUT_Parking.entity.LogoutUsers;
import com.example.DUT_Parking.entity.RegisteredUsers;
import com.example.DUT_Parking.entity.UsersProfile;
import com.example.DUT_Parking.exception_handling.AppException;
import com.example.DUT_Parking.repository.LoginUserRepo;
import com.example.DUT_Parking.repository.LogoutUserRepo;
import com.example.DUT_Parking.repository.RegisteredUserRepo;
import com.example.DUT_Parking.repository.UsersProfileRepo;
import com.example.DUT_Parking.respond.AuthenticationRespond;
import com.example.DUT_Parking.respond.GetRegisteredUsers;
import com.example.DUT_Parking.services.impl.AuthenticationImpl;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;

import java.text.ParseException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.sql.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class AuthenticationServiceTest {

    @InjectMocks
    public AuthenticationImpl authenticationService;

    @Mock
    private UsersProfileRepo usersProfileRepo;

    @Mock
    private RegisteredUserRepo registeredUserRepo;

    @Mock
    private LoginUserRepo loginUserRepo;

    @Mock
    private LogoutUserRepo logoutUserRepo;

    @Mock
    private JWTClaimsSet jwtClaimsSet;

    @Mock
    private SignedJWT signedJWT;

    @Mock
    private JWTParser jwtParser;

    @Mock
    private MACVerifier macVerifier;

    //Test for func authenticated(AuthenticationRequest request)
    @Test
    @DisplayName("Test login function, not duplicate then save " +
            "- Login success and save to LoginUsers")
    void testLogin_SaveData_Success() throws JOSEException {
        AuthenticationRequest request = AuthenticationRequest.builder()
                .email("test@email.com")
                .password("password")
                .build();

        RegisteredUsers registeredUsers = RegisteredUsers.builder()
                .id(1L)
                .email(request.getEmail())
                .password(request.getPassword())
                .build();

        UsersProfile usersProfile = UsersProfile.builder()
                .id(1L)
                .email("test@email.com")
                .password("password")
                .build();

        var token = "abcxyz";

        Mockito.when(registeredUserRepo.findByEmail(request.getEmail())).thenReturn(registeredUsers);
        Mockito.when(usersProfileRepo.findByEmail(request.getEmail())).thenReturn(usersProfile);
        AuthenticationImpl spy = Mockito.spy(authenticationService);
        Mockito.doReturn(token).when(spy).generateToken(usersProfile);
        Mockito.when(loginUserRepo.findByRegisteredUsers(registeredUsers)).thenReturn(null);

        var respond = spy.authenticated(request);

        assertEquals("abcxyz", respond.getToken());
        assertTrue(respond.isAuthenticated());
        verify(loginUserRepo).save(Mockito.any(LoginUsers.class));

    }

    @Test
    @DisplayName("Test login function, duplicate so not save " +
            "- Login success but not save to LoginUsers")
    void testLogin_NotSaveData_Success() throws JOSEException {
        AuthenticationRequest request = AuthenticationRequest.builder()
                .email("test@email.com")
                .password("password")
                .build();

        RegisteredUsers registeredUsers = RegisteredUsers.builder()
                .id(1L)
                .email(request.getEmail())
                .password(request.getPassword())
                .build();

        UsersProfile usersProfile = UsersProfile.builder()
                .id(1L)
                .email("test@email.com")
                .password("password")
                .build();

        var token = "abcxyz";

        Mockito.when(registeredUserRepo.findByEmail(request.getEmail())).thenReturn(registeredUsers);
        Mockito.when(usersProfileRepo.findByEmail(request.getEmail())).thenReturn(usersProfile);
        AuthenticationImpl spy = Mockito.spy(authenticationService);
        Mockito.doReturn(token).when(spy).generateToken(usersProfile);
        Mockito.when(loginUserRepo.findByRegisteredUsers(registeredUsers)).thenReturn(Mockito.any(LoginUsers.class));

        var respond = spy.authenticated(request);

        assertEquals("abcxyz", respond.getToken());
        assertTrue(respond.isAuthenticated());

    }

    @Test
    @DisplayName("Test login function, wrong password - Login failed ")
    void testLogin_WrongPass_Failed() {
        AuthenticationRequest request = AuthenticationRequest.builder()
                .email("test@email.com")
                .password("password")
                .build();

        RegisteredUsers registeredUsers = RegisteredUsers.builder()
                .id(1L)
                .email("test@email.com")
                .password("password1")
                .build();

        UsersProfile usersProfile = UsersProfile.builder()
                .id(1L)
                .email("test@email.com")
                .password("password")
                .build();

        var token = "abcxyz";

        Mockito.when(registeredUserRepo.findByEmail(request.getEmail())).thenReturn(registeredUsers);
        Mockito.when(usersProfileRepo.findByEmail(request.getEmail())).thenReturn(usersProfile);
        AuthenticationImpl spy = Mockito.spy(authenticationService);

        var exception = assertThrows(AppException.class, () -> spy.authenticated(request));

        assertEquals(1006, exception.getErrorCode().getCode());
        assertEquals("Unauthenticated",exception.getMessage());

    }

    @Test
    @DisplayName("Test login function, email not exist in database - Login failed ")
    void testLogin_WrongEmail_Failed() {
        AuthenticationRequest request = AuthenticationRequest.builder()
                .email("test@email.com")
                .password("password")
                .build();

        var token = "abcxyz";

        Mockito.when(registeredUserRepo.findByEmail(request.getEmail())).thenReturn(null);
        Mockito.when(usersProfileRepo.findByEmail(request.getEmail())).thenReturn(Mockito.mock(UsersProfile.class));
        AuthenticationImpl spy = Mockito.spy(authenticationService);

        var exception = assertThrows(AppException.class, () -> spy.authenticated(request));

        assertEquals(1005, exception.getErrorCode().getCode());
        assertEquals("User not existed",exception.getMessage());

    }

    // Test for function logout(LogoutRequest request)
    @Test
    @DisplayName("Test logout function - Logout success")
    void testLogout_Success() throws ParseException, JOSEException {
        String token = "abcxyz";
        String object = "test@gmail.com";
        LocalDate localExpiryDate = LocalDate.of(2025 , 12 , 31);
        Date expiryDate = Date.valueOf(localExpiryDate);

        LogoutRequest request = LogoutRequest.builder()
                .token(token)
                .build();

        RegisteredUsers registeredUsers = RegisteredUsers.builder()
                .id(1L)
                .email(object)
                .password("password")
                .build();

        LoginUsers loginUsers = LoginUsers.builder()
                .id(1L)
                .email(object)
                .registeredUsers(registeredUsers)
                .build();

        LogoutUsers logoutUsers = LogoutUsers.builder()
                .email(object)
                .expiryDate(expiryDate)
                .registeredUsers(registeredUsers)
                .build();

        AuthenticationImpl spy = Mockito.spy(authenticationService);
        Mockito.doReturn(signedJWT).when(spy).verify_token(request.getToken());
        Mockito.doReturn(jwtClaimsSet).when(signedJWT).getJWTClaimsSet();
        Mockito.doReturn(object).when(jwtClaimsSet).getSubject();
        Mockito.when(registeredUserRepo.findByEmail(object)).thenReturn(registeredUsers);
        Mockito.doReturn(expiryDate).when(jwtClaimsSet).getExpirationTime();
        Mockito.when(loginUserRepo.findByRegisteredUsers(registeredUsers))
                .thenReturn(loginUsers);

        spy.logout(request);

        verify(loginUserRepo).delete(loginUsers);
        verify(logoutUserRepo).save(logoutUsers);
    }

    // Test for function generateToken(UsersProfile usersProfile)
    @Test
    @DisplayName("Test function generate token - Generate success")
    void testGenerateToken_Success() throws ParseException, JOSEException {
        UsersProfile usersProfile = UsersProfile.builder()
                .id(1L)
                .email("test@email.com")
                .password("password")
                .build();

        AuthenticationImpl spy = Mockito.spy(authenticationService);
        var signer_key = AuthenticationImpl.signer_key;

        var respond = spy.generateToken(usersProfile);
        var signJWT = SignedJWT.parse(respond);
        var jwtClaimsSet = signJWT.getJWTClaimsSet();

        assertEquals("test@email.com", jwtClaimsSet.getSubject());
    }

    @Test
    @DisplayName("Test function generate token - Generate failed")
    void testGenerateToken_Failed() throws ParseException, JOSEException {
        UsersProfile usersProfile = UsersProfile.builder()
                .id(1L)
                .email("test@email.com")
                .password("password")
                .build();

        AuthenticationImpl spy = Mockito.spy(authenticationService);
        var signer_key = AuthenticationImpl.signer_key;

        Mockito.doThrow(JOSEException.class).when(spy).generateToken(Mockito.any(UsersProfile.class));

        assertThrows(JOSEException.class, () -> spy.generateToken(usersProfile));

    }

    // Test for function introspect()
    @Test
    @DisplayName("Test function introspect - Introspect success")
    void testIntrospect_Success() throws ParseException, JOSEException {
        String token = "abcxyz";
        IntrospectLoginToken request = IntrospectLoginToken.builder()
                .token(token)
                .build();
        var introspect_token = request.getToken();

        AuthenticationImpl spy = Mockito.spy(authenticationService);

        Mockito.doReturn(signedJWT).when(spy).verify_token(introspect_token);

        var respond = spy.introspect(request);

        assertTrue(respond.isValid());
        assertTrue(respond.isValid_expired());
    }

    @Test
    @DisplayName("Test function introspect - Introspect failed")
    void testIntrospect_Failed() throws ParseException, JOSEException {
        String token = "abcxyz";
        IntrospectLoginToken request = IntrospectLoginToken.builder()
                .token(token)
                .build();
        var introspect_token = request.getToken();

        AuthenticationImpl spy = Mockito.spy(authenticationService);

        Mockito.doThrow(AppException.class).when(spy).verify_token(introspect_token);

        var respond = spy.introspect(request);

        assertFalse(respond.isValid());
        assertFalse(respond.isValid_expired());
    }

    // Test for function getAllUsers()
    @Test
    @DisplayName("Test function to get all Login users - Get success")
    void testGetAllLoginUsers_Success() throws ParseException, JOSEException {
        LoginUsers loginUsers = LoginUsers.builder()
                .id(1L)
                .email("test@email.com")
                .build();
        List<LoginUsers> loginUsersList = Collections.singletonList(loginUsers);

        Mockito.doReturn(loginUsersList).when(loginUserRepo).findAll();

        AuthenticationImpl spy = Mockito.spy(authenticationService);

        var respond = spy.getAllUsers();

        assertEquals(1L, respond.get(0).getId());
        assertEquals("test@email.com", respond.get(0).getEmail());
    }

    // Test for function getAllLogoutUsers()
    @Test
    @DisplayName("Test function to get all Logout users - Get success")
    void testGetAllLogoutUsers_Success() throws ParseException, JOSEException {
        LogoutUsers logoutUsers = LogoutUsers.builder()
                .id(1L)
                .email("test@email.com")
                .build();
        List<LogoutUsers> logoutUsersList = Collections.singletonList(logoutUsers);

        Mockito.doReturn(logoutUsersList).when(logoutUserRepo).findAll();

        AuthenticationImpl spy = Mockito.spy(authenticationService);

        var respond = spy.getAllLogoutUsers();

        assertEquals(1L, respond.get(0).getId());
        assertEquals("test@email.com", respond.get(0).getEmail());
    }

    // Test for function verify_token()
    @Test
    @DisplayName("Test function verify token all clear state - Verify success")
    void testVerifyToken_Success() throws ParseException, JOSEException {
        var signer_key = AuthenticationImpl.signer_key;
        AuthenticationImpl spy = Mockito.spy(authenticationService);
        String token = "abcxyz";

        Mockito.doReturn(signedJWT).when(spy).verify_token(token);

        var respond = spy.verify_token(token);

        assertNotNull(respond,"Verify test token success");
    }

    @Test
    @DisplayName("Test function verify token invalid token - Verify failed")
    void testVerifyToken_InvalidToken_Failed() throws ParseException, JOSEException {
        var signer_key = AuthenticationImpl.signer_key;
        AuthenticationImpl spy = Mockito.spy(authenticationService);
        String token = "abcxyz";
        String object = "test@gmail.com";
        LocalDate expiryLocalDate = LocalDate.of(2025 , 12,31);
        Date expiryDate = Date.valueOf(expiryLocalDate);

        RegisteredUsers registeredUsers = RegisteredUsers.builder()
                .id(1L)
                .email("test@gmail.com")
                .build();

        Mockito.doReturn(signedJWT).when(jwtParser).parse(token);
        Mockito.doReturn(jwtClaimsSet).when(signedJWT).getJWTClaimsSet();
        Mockito.doReturn(object).when(jwtClaimsSet).getSubject();
        Mockito.doReturn(expiryDate).when(jwtClaimsSet).getExpirationTime();
        Mockito.when(registeredUserRepo.findByEmail(object)).thenReturn(registeredUsers);
        Mockito.doReturn(false).when(signedJWT).verify(any(MACVerifier.class));

        var exception = assertThrows(AppException.class , () -> spy.verify_token(token));

        assertEquals(1006,exception.getErrorCode().getCode());
        assertEquals("Unauthenticated" , exception.getMessage());
    }

    @Test
    @DisplayName("Test function verify token token expired - Verify failed")
    void testVerifyToken_TokenExpired_Failed() throws ParseException, JOSEException {
        var signer_key = AuthenticationImpl.signer_key;
        AuthenticationImpl spy = Mockito.spy(authenticationService);
        String token = "abcxyz";
        String object = "test@gmail.com";
        LocalDate expiryLocalDate = LocalDate.of(2000 , 12,31);
        Date expiryDate = Date.valueOf(expiryLocalDate);

        RegisteredUsers registeredUsers = RegisteredUsers.builder()
                .id(1L)
                .email("test@gmail.com")
                .build();

        Mockito.doReturn(signedJWT).when(jwtParser).parse(token);
        Mockito.doReturn(jwtClaimsSet).when(signedJWT).getJWTClaimsSet();
        Mockito.doReturn(object).when(jwtClaimsSet).getSubject();
        Mockito.doReturn(expiryDate).when(jwtClaimsSet).getExpirationTime();
        Mockito.when(registeredUserRepo.findByEmail(object)).thenReturn(registeredUsers);
        Mockito.doReturn(true).when(signedJWT).verify(any(MACVerifier.class));

        var exception = assertThrows(AppException.class , () -> spy.verify_token(token));

        assertEquals(1006,exception.getErrorCode().getCode());
        assertEquals("Unauthenticated" , exception.getMessage());
    }

    @Test
    @DisplayName("Test function verify token token deactivated - Verify failed")
    void testVerifyToken_TokenDeactivated_Failed() throws ParseException, JOSEException {
        var signer_key = AuthenticationImpl.signer_key;
        AuthenticationImpl spy = Mockito.spy(authenticationService);
        String token = "abcxyz";
        String object = "test@gmail.com";
        LocalDate expiryLocalDate = LocalDate.of(2025 , 12,31);
        Date expiryDate = Date.valueOf(expiryLocalDate);

        RegisteredUsers registeredUsers = RegisteredUsers.builder()
                .id(1L)
                .email("test@gmail.com")
                .build();

        Mockito.doReturn(signedJWT).when(jwtParser).parse(token);
        Mockito.doReturn(jwtClaimsSet).when(signedJWT).getJWTClaimsSet();
        Mockito.doReturn(object).when(jwtClaimsSet).getSubject();
        Mockito.doReturn(expiryDate).when(jwtClaimsSet).getExpirationTime();
        Mockito.when(registeredUserRepo.findByEmail(object)).thenReturn(registeredUsers);
        Mockito.doReturn(true).when(signedJWT).verify(any(MACVerifier.class));
        Mockito.when(logoutUserRepo.existsByRegisteredUsers(registeredUsers)).thenReturn(true);

        var exception = assertThrows(AppException.class , () -> spy.verify_token(token));

        assertEquals(1006,exception.getErrorCode().getCode());
        assertEquals("Unauthenticated" , exception.getMessage());
    }
}
