package com.example.DUT_Parking.services;

import com.example.DUT_Parking.DTO.PassRequest;
import com.example.DUT_Parking.configuration.JWTParser;
import com.example.DUT_Parking.entity.PassMonitor;
import com.example.DUT_Parking.entity.Tickets;
import com.example.DUT_Parking.entity.UserTicketsInfo;
import com.example.DUT_Parking.entity.UsersProfile;
import com.example.DUT_Parking.enums.TicketStatus;
import com.example.DUT_Parking.repository.PassMonitorRepo;
import com.example.DUT_Parking.repository.UserTicketsRepo;
import com.example.DUT_Parking.repository.UsersProfileRepo;
import com.example.DUT_Parking.respond.GetAllPassDataRespond;
import com.example.DUT_Parking.services.impl.PassMonitorImpl;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

@ExtendWith(MockitoExtension.class)
public class PassMonitorServiceTest {
    @InjectMocks
    private PassMonitorImpl passMonitorService;

    @Mock
    private PassMonitorRepo passMonitorRepo;

    @Mock
    private UserTicketsRepo userTicketsRepo;

    @Mock
    private UsersProfileRepo usersProfileRepo;

    @Mock
    private SignedJWT signedJWT;

    @Mock
    private JWTClaimsSet jwtClaimsSet;

    @Mock
    private JWTParser jwtParser;

    // Test for function HandlePassData()
    @Test
    @DisplayName("Test handle pass data function - PASS decision")
    void testHandlePassDataFunction_Pass() throws ParseException {
        String passToken = "abcxyz";
        Long id = 1L;
        String decision = "PASS";

        UsersProfile usersProfile = new UsersProfile();
        UserTicketsInfo userTicketsInfo = Mockito.mock(UserTicketsInfo.class);
        PassRequest request = PassRequest.builder()
                .passToken(passToken)
                .build();

        Mockito.when(userTicketsInfo.getUsersProfile()).thenReturn(usersProfile);
        Mockito.when(userTicketsRepo.findById(id)).thenReturn(userTicketsInfo);

        Mockito.doReturn(signedJWT).when(jwtParser).parse(request.getPassToken());
        Mockito.doReturn(jwtClaimsSet).when(signedJWT).getJWTClaimsSet();
        Mockito.doReturn(id).when(jwtClaimsSet).getLongClaim("id");
        Mockito.doReturn(decision).when(jwtClaimsSet).getStringClaim("decision");

        PassMonitor passMonitor = PassMonitor.builder()
                .usersProfile(usersProfile)
                .userTicketsInfo(userTicketsInfo)
                .decision(decision)
                .build();

        PassMonitorImpl spy = Mockito.spy(passMonitorService);

        spy.HandlePassData(request);

        Mockito.verify(userTicketsInfo , Mockito.never()).setStatus(TicketStatus.EXPIRED.name());
        Mockito.verify(passMonitorRepo).save(passMonitor);
        Mockito.verify(userTicketsRepo).save(userTicketsInfo);
        Mockito.verify(usersProfileRepo).save(usersProfile);
    }

    @Test
    @DisplayName("Test handle pass data function - NOT PASS decision")
    void testHandlePassDataFunction_NotPass() throws ParseException {
        String passToken = "abcxyz";
        Long id = 1L;
        String decision = "NOT PASS";

        UsersProfile usersProfile = new UsersProfile();
        UserTicketsInfo userTicketsInfo = Mockito.mock(UserTicketsInfo.class);
        PassRequest request = PassRequest.builder()
                .passToken(passToken)
                .build();

        Mockito.when(userTicketsInfo.getUsersProfile()).thenReturn(usersProfile);
        Mockito.when(userTicketsRepo.findById(id)).thenReturn(userTicketsInfo);

        Mockito.doReturn(signedJWT).when(jwtParser).parse(request.getPassToken());
        Mockito.doReturn(jwtClaimsSet).when(signedJWT).getJWTClaimsSet();
        Mockito.doReturn(id).when(jwtClaimsSet).getLongClaim("id");
        Mockito.doReturn(decision).when(jwtClaimsSet).getStringClaim("decision");

        PassMonitor passMonitor = PassMonitor.builder()
                .usersProfile(usersProfile)
                .userTicketsInfo(userTicketsInfo)
                .decision(decision)
                .build();

        PassMonitorImpl spy = Mockito.spy(passMonitorService);

        spy.HandlePassData(request);

        Mockito.verify(userTicketsInfo).setStatus(TicketStatus.EXPIRED.name());
        Mockito.verify(passMonitorRepo).save(passMonitor);
        Mockito.verify(userTicketsRepo).save(userTicketsInfo);
        Mockito.verify(usersProfileRepo).save(usersProfile);
    }

    // Test for function getAllPassData()
    @Test
    @DisplayName("Test get all pass data function - Get success")
    void testGetAllPassDataFunction_GetSuccess() throws ParseException {
        List<GetAllPassDataRespond> getAllPassDataResponds = new ArrayList<>();
        List<PassMonitor> passMonitors = new ArrayList<>();
        PassMonitorImpl spy = Mockito.spy(passMonitorService);

        Mockito.when(passMonitorRepo.findAll()).thenReturn(passMonitors);

        var respond = spy.getAllPassData();

        Assertions.assertEquals(getAllPassDataResponds,respond);
    }

    // Test for function deleteAllPassData()
    @Test
    @DisplayName("Test reset pass data function - Reset success")
    void testResetPassDataFunction_ResetSuccess() throws ParseException {
        PassMonitorImpl spy = Mockito.spy(passMonitorService);

        Mockito.doNothing().when(passMonitorRepo).deleteAll();

        spy.deleteAllPassData();

        Mockito.verify(passMonitorRepo).deleteAll();
    }
}
