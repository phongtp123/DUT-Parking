package com.example.DUT_Parking.services;

import com.example.DUT_Parking.DTO.BuyTicketRequest;
import com.example.DUT_Parking.DTO.TicketCreate;
import com.example.DUT_Parking.configuration.JWTParser;
import com.example.DUT_Parking.configuration.SecurityHolder;
import com.example.DUT_Parking.entity.Tickets;
import com.example.DUT_Parking.entity.UserTicketsInfo;
import com.example.DUT_Parking.entity.UsersProfile;
import com.example.DUT_Parking.exception_handling.AppException;
import com.example.DUT_Parking.repository.TicketsRepo;
import com.example.DUT_Parking.repository.UserTicketsRepo;
import com.example.DUT_Parking.repository.UsersProfileRepo;
import com.example.DUT_Parking.respond.*;
import com.example.DUT_Parking.services.impl.TicketImpl;
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
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;

import java.sql.Date;
import java.text.ParseException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@ExtendWith(MockitoExtension.class)
public class TicketServiceTest {
    @InjectMocks
    private TicketImpl ticketService;

    @Mock
    private UsersProfileRepo usersProfileRepo;

    @Mock
    private TicketsRepo ticketsRepo;

    @Mock
    private UserTicketsRepo userTicketsRepo;

    @Mock
    private SecurityHolder securityHolder;

    @Mock
    private Authentication authentication;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private JWTParser jwtParser;

    @Mock
    private SignedJWT signedJWT;

    @Mock
    private JWTClaimsSet jwtClaimsSet;

    // Test for function createTicket()
    @Test
    @DisplayName("Test create new ticket type function - Create success")
    void createNewTicketType_Success() {
        TicketCreate ticketCreate = TicketCreate.builder()
                .ticketId("test")
                .ticketName("test")
                .menhgia(9999)
                .build();

        TicketRespond ticketRespond = TicketRespond.builder()
                .message("Ticket create success!")
                .build();

        TicketImpl spy = Mockito.spy(ticketService);

        var respond = spy.createTicket(ticketCreate);

        Assertions.assertEquals(ticketRespond, respond);
        Mockito.verify(ticketsRepo).save(Mockito.any(Tickets.class));
    }

    // Test for function buyTicket()
    @Test
    @DisplayName("Test buy ticket function - Buy success")
    void buyTicket_Success() {
        String rawinfo = "test@gmail.com";
        List<UserTicketsInfo> userTicketsInfoList = new ArrayList<>() ;

        UsersProfile usersProfile = UsersProfile.builder()
                .email("test@gmail.com")
                .sodu(999999)
                .userTicketsInfos(userTicketsInfoList)
                .build();

        Tickets tickets = Tickets.builder()
                .ticketName("test")
                .menhgia(9999)
                .userTicketsInfos(userTicketsInfoList)
                .build();

        BuyTicketRequest buyTicketRequest = BuyTicketRequest.builder()
                .name("test")
                .build();

        BuyTicketRespond buyTicketRespond = BuyTicketRespond.builder()
                .status(true)
                .message(String.format("Mua thanh cong %s , vui long kiem tra Ticket List cua ban" , tickets.getTicketName()))
                .build();

        TicketImpl spy = Mockito.spy(ticketService);

        Mockito.doReturn(securityContext).when(securityHolder).getContext();
        Mockito.doReturn(authentication).when(securityContext).getAuthentication();
        Mockito.doReturn(rawinfo).when(authentication).getName();
        Mockito.when(usersProfileRepo.findByEmail(rawinfo)).thenReturn(usersProfile);
        Mockito.when(ticketsRepo.findByTicketName(buyTicketRequest.getName())).thenReturn(tickets);

        var respond = spy.buyTicket(buyTicketRequest);

        Assertions.assertEquals(buyTicketRespond, respond);

        Mockito.verify(ticketsRepo).save(tickets);
        Mockito.verify(userTicketsRepo).save(Mockito.any(UserTicketsInfo.class));
        Mockito.verify(usersProfileRepo).save(usersProfile);

    }

    @Test
    @DisplayName("Test buy ticket function - INSUFFICIENT_FUNDS failed")
    void buyTicket_InsufficientFund_Failed() {
        String rawinfo = "test@gmail.com";
        List<UserTicketsInfo> userTicketsInfoList = new ArrayList<>() ;

        UsersProfile usersProfile = UsersProfile.builder()
                .email("test@gmail.com")
                .sodu(0)
                .userTicketsInfos(userTicketsInfoList)
                .build();

        Tickets tickets = Tickets.builder()
                .ticketName("test")
                .menhgia(9999)
                .userTicketsInfos(userTicketsInfoList)
                .build();

        BuyTicketRequest buyTicketRequest = BuyTicketRequest.builder()
                .name("test")
                .build();

        BuyTicketRespond buyTicketRespond = BuyTicketRespond.builder()
                .status(true)
                .message(String.format("Mua thanh cong %s , vui long kiem tra Ticket List cua ban" , tickets.getTicketName()))
                .build();

        TicketImpl spy = Mockito.spy(ticketService);

        Mockito.doReturn(securityContext).when(securityHolder).getContext();
        Mockito.doReturn(authentication).when(securityContext).getAuthentication();
        Mockito.doReturn(rawinfo).when(authentication).getName();
        Mockito.when(usersProfileRepo.findByEmail(rawinfo)).thenReturn(usersProfile);
        Mockito.when(ticketsRepo.findByTicketName(buyTicketRequest.getName())).thenReturn(tickets);

        var exception = Assertions.assertThrows(AppException.class , () -> spy.buyTicket(buyTicketRequest));

        Assertions.assertEquals(1014, exception.getErrorCode().getCode());
        Assertions.assertEquals("Insufficient funds , your fund are not enough to buy this ticket"
                , exception.getMessage());

    }

    @Test
    @DisplayName("Test buy ticket function - TICKET_NOT_EXISTED failed")
    void buyTicket_InvalidTicketName_Failed() {
        String rawinfo = "test@gmail.com";
        List<UserTicketsInfo> userTicketsInfoList = new ArrayList<>() ;

        UsersProfile usersProfile = UsersProfile.builder()
                .email("test@gmail.com")
                .sodu(0)
                .userTicketsInfos(userTicketsInfoList)
                .build();

        BuyTicketRequest buyTicketRequest = BuyTicketRequest.builder()
                .name("test")
                .build();

        TicketImpl spy = Mockito.spy(ticketService);

        Mockito.doReturn(securityContext).when(securityHolder).getContext();
        Mockito.doReturn(authentication).when(securityContext).getAuthentication();
        Mockito.doReturn(rawinfo).when(authentication).getName();
        Mockito.when(usersProfileRepo.findByEmail(rawinfo)).thenReturn(usersProfile);
        Mockito.when(ticketsRepo.findByTicketName(buyTicketRequest.getName())).thenReturn(null);

        var exception = Assertions.assertThrows(AppException.class , () -> spy.buyTicket(buyTicketRequest));

        Assertions.assertEquals(1015, exception.getErrorCode().getCode());
        Assertions.assertEquals("Ticket not existed", exception.getMessage());

    }

    // Test for function getUserTicketsList()
    @Test
    @DisplayName("Test user-based introspect user ticket list function - Introspect success")
    void testGetUserTicketList_Success() {
        String rawinfo = "test@gmail.com";

        List<UserTicketsInfo> userTicketsInfoList = new ArrayList<>() ;

        List<GetUserTicketsListRespond> userTicketsListRespondList = new ArrayList<>() ;

        TicketImpl spy = Mockito.spy(ticketService);

        Mockito.doReturn(securityContext).when(securityHolder).getContext();
        Mockito.doReturn(authentication).when(securityContext).getAuthentication();
        Mockito.doReturn(rawinfo).when(authentication).getName();
        Mockito.when(userTicketsRepo.findAllByEmail(rawinfo)).thenReturn(userTicketsInfoList);

        var respond = spy.getUserTicketsList();

        Assertions.assertEquals(userTicketsListRespondList, respond);
    }

    // Test for function UserDeleteTicket()
    @Test
    @DisplayName("Test user-based delete ticket in user tickets list - Delete success")
    void testUserDeleteTicket_Success() {
        Long id = 1L;
        Mockito.doNothing().when(userTicketsRepo).deleteById(id);

        TicketImpl spy = Mockito.spy(ticketService);

        spy.UserDeleteTicket(id);
    }

    @Test
    @DisplayName("Test user-based delete ticket in user tickets list - TICKET_NOT_EXISTED failed")
    void testUserDeleteTicket_Failed() {
        Long id = 1L;
        Mockito.doThrow(EmptyResultDataAccessException.class).when(userTicketsRepo).deleteById(id);

        TicketImpl spy = Mockito.spy(ticketService);

        var exception = Assertions.assertThrows(AppException.class , () -> spy.UserDeleteTicket(id));

        Assertions.assertEquals(1015, exception.getErrorCode().getCode());
        Assertions.assertEquals("Ticket not existed", exception.getMessage());
    }

    // Test for function AdminDeleteTicket()
    @Test
    @DisplayName("Test admin-based delete ticket in user tickets list - Delete success")
    void testAdminDeleteTicket_Success() {
        Long id = 1L;
        Mockito.doNothing().when(userTicketsRepo).deleteById(id);

        TicketImpl spy = Mockito.spy(ticketService);

        spy.AdminDeleteTicket(id);
    }

    @Test
    @DisplayName("Test admin-based delete ticket in user tickets list - TICKET_NOT_EXISTED failed")
    void testAdminDeleteTicket_Failed() {
        Long id = 1L;
        Mockito.doThrow(EmptyResultDataAccessException.class).when(userTicketsRepo).deleteById(id);

        TicketImpl spy = Mockito.spy(ticketService);

        var exception = Assertions.assertThrows(AppException.class , () -> spy.AdminDeleteTicket(id));

        Assertions.assertEquals(1015, exception.getErrorCode().getCode());
        Assertions.assertEquals("Ticket not existed", exception.getMessage());
    }

    // Test for function getAllUserTickets()
    @Test
    @DisplayName("Test get all user tickets in system function - Get success")
    void testGetAllUserTickets_Success() {
        List<UserTicketsInfo> userTicketsInfoList = new ArrayList<>() ;

        List<GetAllUserTicketsListRespond> userTicketsListRespondList = new ArrayList<>() ;

        TicketImpl spy = Mockito.spy(ticketService);

        Mockito.when(userTicketsRepo.findAll()).thenReturn(userTicketsInfoList);

        var respond = spy.getAllUserTickets();

        Assertions.assertEquals(userTicketsListRespondList, respond);
    }

    // Test for function findUserTicket()
    @Test
    @DisplayName("Test find user ticket by MSSV - Find success")
    void testFindUserTicketByMSSV_Success() {
        String mssv = "test";

        Tickets tickets = new Tickets();

        UsersProfile usersProfile = new UsersProfile();

        UserTicketsInfo userTicketsInfo = UserTicketsInfo.builder()
                .tickets(tickets)
                .usersProfile(usersProfile)
                .build();

        List<UserTicketsInfo> userTicketsInfoList = new ArrayList<>() ;
        userTicketsInfoList.add(userTicketsInfo);

        GetAllUserTicketsListRespond getAllUserTicketsListRespond = new GetAllUserTicketsListRespond();

        List<GetAllUserTicketsListRespond> userTicketsListRespondList = new ArrayList<>() ;
        userTicketsListRespondList.add(getAllUserTicketsListRespond);

        TicketImpl spy = Mockito.spy(ticketService);

        Mockito.when(userTicketsRepo.findAllByMSSV(mssv)).thenReturn(userTicketsInfoList);

        var respond = spy.findUserTicket(mssv);

        Assertions.assertEquals(userTicketsListRespondList, respond);
    }

    @Test
    @DisplayName("Test find user ticket by MSSV - MSSV_NOT_EXISTED failed")
    void testFindUserTicketByMSSV_Failed() {
        String mssv = "test";

        TicketImpl spy = Mockito.spy(ticketService);

        Mockito.when(userTicketsRepo.findAllByMSSV(mssv)).thenReturn(Collections.emptyList());

        var exception = Assertions.assertThrows(AppException.class , () -> spy.findUserTicket(mssv));

        Assertions.assertEquals(1016, exception.getErrorCode().getCode());
        Assertions.assertEquals("User with this MSSV not existed", exception.getMessage());
    }

    // Test for function enableTicket()
    @Test
    @DisplayName("Test enable ticket - Enable success")
    void testEnableTicket_Success() throws ParseException {
        Long id = 1L;
        String email = "test@gmail.com";
        Tickets tickets = Tickets.builder()
                .ticketName("test")
                .build();
        UsersProfile usersProfile = UsersProfile.builder()
                .email(email)
                .hovaten("Test")
                .build();
        UserTicketsInfo userTicketsInfo = UserTicketsInfo.builder()
                .tickets(tickets)
                .usersProfile(usersProfile)
                .build();
        var ticketToken = "abcxyz";
        LocalDate issueLocalDate = LocalDate.now();
        Date issueDate = Date.valueOf(issueLocalDate);
        LocalDate expiryLocalDate = issueLocalDate.plusDays(1);
        Date expiryDate = Date.valueOf(expiryLocalDate);
        byte[] qrCode = new byte[10];

        EnableTicketRespond enableTicketRespond = EnableTicketRespond.builder()
                .status(true)
                .message("Enable ticket success")
                .ticketToken(ticketToken)
                .build();

        TicketImpl spy = Mockito.spy(ticketService);

        Mockito.when(userTicketsRepo.findById(id)).thenReturn(userTicketsInfo);
        Mockito.when(usersProfileRepo.findByEmail(email)).thenReturn(usersProfile);
        Mockito.when(spy.generateTicket(id)).thenReturn(ticketToken);
        Mockito.doReturn(signedJWT).when(jwtParser).parse(ticketToken);
        Mockito.doReturn(jwtClaimsSet).when(signedJWT).getJWTClaimsSet();
        Mockito.doReturn(issueDate).when(jwtClaimsSet).getIssueTime();
        Mockito.doReturn(expiryDate).when(jwtClaimsSet).getExpirationTime();
        Mockito.when(spy.qr_code_maker(ticketToken)).thenReturn(qrCode);

        var respond = spy.enableTicket(id);

        Assertions.assertEquals(enableTicketRespond, respond);
        Assertions.assertEquals("ENABLE", userTicketsInfo.getStatus());
        Assertions.assertEquals(issueDate, userTicketsInfo.getIssueDate());
        Assertions.assertEquals(expiryDate, userTicketsInfo.getExpiryDate());

        Mockito.verify(userTicketsRepo).save(userTicketsInfo);
    }

    @Test
    @DisplayName("Test enable ticket - TICKET_NOT_EXISTED failed")
    void testEnableTicket_Failed_01() throws ParseException {
        Long id = 1L;

        TicketImpl spy = Mockito.spy(ticketService);

        Mockito.when(userTicketsRepo.findById(id)).thenReturn(null);

        var exception = Assertions.assertThrows(AppException.class , () -> spy.enableTicket(id));

        Assertions.assertEquals(1015, exception.getErrorCode().getCode());
        Assertions.assertEquals("Ticket not existed", exception.getMessage());
    }

    // Test for function getAllTickets()
    @Test
    @DisplayName("Test get list of ticket type function - Get success")
    void testGetListOfTicketTypeFunction_Success() {
        List<Tickets> ticketsList = new ArrayList<>() ;
        List<GetTicketTypeList> getTicketTypeListList = new ArrayList<>() ;
        TicketImpl spy = Mockito.spy(ticketService);

        Mockito.when(ticketsRepo.findAll()).thenReturn(ticketsList);

        var respond = spy.getAllTickets();

        Assertions.assertEquals(getTicketTypeListList, respond);
    }

    // Test for function deleteTicket()
    @Test
    @DisplayName("Test delete ticket type function - Delete success")
    void testDeleteTicketTypeFunction_Success() {
        String ticketId = "test";

        TicketImpl spy = Mockito.spy(ticketService);

        Mockito.doNothing().when(ticketsRepo).deleteById(ticketId);

        spy.deleteTicket(ticketId);

        Mockito.verify(ticketsRepo).deleteById(ticketId);
    }

    @Test
    @DisplayName("Test delete ticket type function - TICKET_NOT_EXISTED failed")
    void testDeleteTicketTypeFunction_Failed() {
        String ticketId = "test";

        TicketImpl spy = Mockito.spy(ticketService);

        Mockito.doThrow(EmptyResultDataAccessException.class).when(ticketsRepo).deleteById(ticketId);

        var exception = Assertions.assertThrows(AppException.class , () -> spy.deleteTicket(ticketId));

        Assertions.assertEquals(1015, exception.getErrorCode().getCode());
        Assertions.assertEquals("Ticket not existed", exception.getMessage());
    }
}
