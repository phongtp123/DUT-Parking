package com.example.DUT_Parking.controller;

import com.example.DUT_Parking.DTO.BuyTicketRequest;
import com.example.DUT_Parking.DTO.RegisterRequest;
import com.example.DUT_Parking.DTO.TicketCreate;
import com.example.DUT_Parking.respond.*;
import com.example.DUT_Parking.services.AdminServices;
import com.example.DUT_Parking.services.RegisterService;
import com.example.DUT_Parking.services.UserServices;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.qameta.allure.Feature;
import io.qameta.allure.Link;
import io.qameta.allure.TmsLink;
import org.apiguardian.api.API;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;

import javax.xml.crypto.Data;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collections;
import java.sql.Date;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Ticket Controller Test")
public class TicketControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    @Qualifier("ticketImpl")
    private UserServices userServices;

    @MockBean
    @Qualifier("ticketImpl")
    private AdminServices adminServices;

    @Autowired
    ObjectMapper objectMapper;

    //Test for POST services/ticket/create-ticket
    @Feature("Create Ticket Type")
    @Test
    @TmsLink("47")
    @DisplayName("Test Create Ticket With Admin Role - Create Success")
    @WithMockUser(username = "admin@gmail.com" , roles = {"ADMIN"})
    @Link(name = "TK-01" , type = "task")
    void testCreateTicket_AdminRole_Success() throws Exception {
        var ticketName = "VE NGAY";
        var menhgia = 9999;

        TicketRespond ticketRespond = TicketRespond.builder()
                .message("Ticket create success!")
                .build();
        TicketCreate ticketCreate = TicketCreate.builder()
                .ticketName(ticketName)
                .menhgia(menhgia)
                .build();
        APIRespond<TicketRespond> apiRespond = APIRespond.<TicketRespond>builder()
                .result(ticketRespond)
                .build();

        String content = objectMapper.writeValueAsString(ticketCreate);

        Mockito.when(adminServices.createTicket(any())).thenReturn(apiRespond.getResult());

        mockMvc.perform(post("/services/ticket/create-ticket")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(content))
                .andExpect(status().isOk())
                .andExpect(jsonPath("result.message").value("Ticket create success!"));
    }

    @Feature("Create Ticket Type")
    @Test
    @TmsLink("48")
    @DisplayName("Test Create Ticket With User Role - Forbidden Failed")
    @WithMockUser(username = "test@gmail.com")
    @Link(name = "TK-02" , type = "task")
    void testCreateTicket_UserRole_Failed() throws Exception {
        var ticketName = "VE NGAY";
        var menhgia = 9999;

        TicketRespond ticketRespond = TicketRespond.builder()
                .message("Ticket create success!")
                .build();
        TicketCreate ticketCreate = TicketCreate.builder()
                .ticketName(ticketName)
                .menhgia(menhgia)
                .build();
        APIRespond<TicketRespond> apiRespond = APIRespond.<TicketRespond>builder()
                .result(ticketRespond)
                .build();

        String content = objectMapper.writeValueAsString(ticketCreate);

        Mockito.when(adminServices.createTicket(any())).thenReturn(apiRespond.getResult());

        mockMvc.perform(post("/services/ticket/create-ticket")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(content))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("code").value(1007))
                .andExpect(jsonPath("message").value("You do not have permission"));
    }

    @Feature("Create Ticket Type")
    @Test
    @TmsLink("49")
    @DisplayName("Test Create Ticket Expired Token - Unauthenticated Failed")
    @WithAnonymousUser
    @Link(name = "TK-03" , type = "task")
    void testCreateTicket_ExpiredToken_Failed() throws Exception {
        var ticketName = "VE NGAY";
        var menhgia = 9999;

        TicketRespond ticketRespond = TicketRespond.builder()
                .message("Ticket create success!")
                .build();
        TicketCreate ticketCreate = TicketCreate.builder()
                .ticketName(ticketName)
                .menhgia(menhgia)
                .build();
        APIRespond<TicketRespond> apiRespond = APIRespond.<TicketRespond>builder()
                .result(ticketRespond)
                .build();

        String content = objectMapper.writeValueAsString(ticketCreate);

        Mockito.when(adminServices.createTicket(any())).thenReturn(apiRespond.getResult());

        mockMvc.perform(post("/services/ticket/create-ticket")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(content))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("code").value(1006))
                .andExpect(jsonPath("message").value("Unauthenticated"));
    }

    //Test for GET /services/ticket/tickets-list
    @Feature("Get Ticket Type")
    @Test
    @TmsLink("50")
    @DisplayName("Test Get Ticket Type List With Admin Role - Get Success")
    @WithMockUser(username = "admin@gmail.com" , roles = {"ADMIN"})
    @Link(name = "TK-04" , type = "task")
    void testGetTicketsList_AdminRole_Success() throws Exception {
        var ticketName = "VE NGAY";
        var menhgia = 9999;
        var ticketId = "VE01";

        GetTicketTypeList getTicketTypeList = GetTicketTypeList.builder()
                .ticketId(ticketId)
                .ticketName(ticketName)
                .menhgia(menhgia)
                .build();

        List<GetTicketTypeList> getTicketTypeLists = Collections.singletonList(getTicketTypeList);

        Mockito.when(adminServices.getAllTickets()).thenReturn(getTicketTypeLists);

        mockMvc.perform(get("/services/ticket/tickets-list"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].ticketId").value(ticketId))
                .andExpect(jsonPath("$[0].ticketName").value(ticketName))
                .andExpect(jsonPath("$[0].menhgia").value(menhgia));
    }

    @Feature("Get Ticket Type")
    @Test
    @TmsLink("51")
    @DisplayName("Test Get Ticket Type List With User Role - Forbidden Failed")
    @WithMockUser(username = "test@gmail.com")
    @Link(name = "TK-05" , type = "task")
    void testGetTicketsList_UserRole_Failed() throws Exception {
        var ticketName = "VE NGAY";
        var menhgia = 9999;
        var ticketId = "VE01";

        GetTicketTypeList getTicketTypeList = GetTicketTypeList.builder()
                .ticketId(ticketId)
                .ticketName(ticketName)
                .menhgia(menhgia)
                .build();

        List<GetTicketTypeList> getTicketTypeLists = Collections.singletonList(getTicketTypeList);

        Mockito.when(adminServices.getAllTickets()).thenReturn(getTicketTypeLists);

        mockMvc.perform(get("/services/ticket/tickets-list"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("code").value(1007))
                .andExpect(jsonPath("message").value("You do not have permission"));
    }

    @Feature("Get Ticket Type")
    @Test
    @TmsLink("52")
    @DisplayName("Test Get Ticket Type List Expired Token - Unauthenticated Failed")
    @WithAnonymousUser
    @Link(name = "TK-06" , type = "task")
    void testGetTicketsList_ExpiredToken_Failed() throws Exception {
        var ticketName = "VE NGAY";
        var menhgia = 9999;
        var ticketId = "VE01";

        GetTicketTypeList getTicketTypeList = GetTicketTypeList.builder()
                .ticketId(ticketId)
                .ticketName(ticketName)
                .menhgia(menhgia)
                .build();

        List<GetTicketTypeList> getTicketTypeLists = Collections.singletonList(getTicketTypeList);

        Mockito.when(adminServices.getAllTickets()).thenReturn(getTicketTypeLists);

        mockMvc.perform(get("/services/ticket/tickets-list"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("code").value(1006))
                .andExpect(jsonPath("message").value("Unauthenticated"));
    }

    //Test for DELETE /services/ticket/tickets-list/{ticket_name}
    @Feature("Delete Ticket Type By Ticket Name")
    @Test
    @TmsLink("53")
    @DisplayName("Test Delete Ticket Type List With Admin Role - Delete Success")
    @WithMockUser(username = "admin@gmail.com" , roles = {"ADMIN"})
    @Link(name = "TK-07" , type = "task")
    void testDeleteTicketType_AdminRole_Success() throws Exception {
        var ticketId = "VE01";

        Mockito.doNothing().when(adminServices).deleteTicket(any());

        mockMvc.perform(delete("/services/ticket/tickets-list/VE01"))
                .andExpect(status().isOk())
                .andExpect(content()
                        .string(String.format("Ticket %s has been delete successfully", ticketId)));
    }

    @Feature("Delete Ticket Type By Ticket Name")
    @Test
    @TmsLink("54")
    @DisplayName("Test Delete Ticket Type List Expired Token - Unauthenticated Failed")
    @WithAnonymousUser
    @Link(name = "TK-08" , type = "task")
    void testDeleteTicketType_ExpiredToken_Failed() throws Exception {

        Mockito.doNothing().when(adminServices).deleteTicket(any());

        mockMvc.perform(delete("/services/ticket/tickets-list/VE01"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("code").value(1006))
                .andExpect(jsonPath("message").value("Unauthenticated"));
    }

    //Test for POST /services/ticket/buy-ticket
    @Feature("Buy Ticket")
    @Test
    @TmsLink("55")
    @DisplayName("Test Buy Ticket With Valid Token - Buy Success")
    @WithMockUser(username = "test@gmail.com")
    @Link(name = "TK-09" , type = "task")
    void testBuyTicket_ValidToken_Success() throws Exception {
        var ticketName = "VE NGAY";

        BuyTicketRequest buyTicketRequest = BuyTicketRequest.builder()
                .name(ticketName)
                .build();

        BuyTicketRespond buyTicketRespond = BuyTicketRespond.builder()
                .status(true)
                .message(String.format("Mua thanh cong %s , vui long kiem tra Ticket List cua ban", ticketName))
                .build();

        APIRespond<BuyTicketRespond> apiRespond = APIRespond.<BuyTicketRespond>builder()
                .result(buyTicketRespond)
                .build();

        objectMapper = new ObjectMapper();
        String content = objectMapper.writeValueAsString(buyTicketRequest);

        Mockito.when(userServices.buyTicket(any())).thenReturn(apiRespond.getResult());

        mockMvc.perform(post("/services/ticket/buy-ticket")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(content))
                .andExpect(status().isOk())
                .andExpect(jsonPath("result.status").value(true))
                .andExpect(jsonPath("result.message")
                        .value(String.format("Mua thanh cong %s , vui long kiem tra Ticket List cua ban", ticketName)));
    }

    @Feature("Buy Ticket")
    @Test
    @TmsLink("56")
    @DisplayName("Test Buy Ticket With Expired Token - Unauthenticated Failed")
    @WithAnonymousUser
    @Link(name = "TK-10" , type = "task")
    void testBuyTicket_ExpiredToken_Failed() throws Exception {
        var ticketName = "VE NGAY";

        BuyTicketRequest buyTicketRequest = BuyTicketRequest.builder()
                .name(ticketName)
                .build();

        BuyTicketRespond buyTicketRespond = BuyTicketRespond.builder()
                .status(true)
                .message(String.format("Mua thanh cong %s , vui long kiem tra Ticket List cua ban", ticketName))
                .build();

        APIRespond<BuyTicketRespond> apiRespond = APIRespond.<BuyTicketRespond>builder()
                .result(buyTicketRespond)
                .build();

        objectMapper = new ObjectMapper();
        String content = objectMapper.writeValueAsString(buyTicketRequest);

        Mockito.when(userServices.buyTicket(any())).thenReturn(apiRespond.getResult());

        mockMvc.perform(post("/services/ticket/buy-ticket")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(content))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("code").value(1006))
                .andExpect(jsonPath("message").value("Unauthenticated"));
    }

    //Test for GET /services/ticket/my-tickets-list
    @Feature("User Get Ticket Wallet")
    @Test
    @TmsLink("57")
    @DisplayName("Test Get My Tickets List - Get Success")
    @WithMockUser(username = "test@gmail.com")
    @Link(name = "TK-11" , type = "task")
    void testGetMyTicketsList_Success() throws Exception {
        var ticketName = "VE NGAY";
        var menhgia = 9999;
        var expiryLocalDate = LocalDate.of(2025,12,31);
        Date expiryDate = Date.valueOf(expiryLocalDate);
        var issueLocalDate = LocalDate.of(2023,12,31);
        Date issueDate = Date.valueOf(issueLocalDate);
        var status = "DISABLED";
        byte[] qrcode = new byte[10];

        GetUserTicketsListRespond getUserTicketsListRespond = GetUserTicketsListRespond.builder()
                .ticketId(1L)
                .ticketName(ticketName)
                .menhgia(menhgia)
                .expiryDate(expiryDate)
                .issueDate(issueDate)
                .status(status)
                .qr_code(qrcode)
                .build();

        List<GetUserTicketsListRespond> getUserTicketsListResponds = Collections.singletonList(getUserTicketsListRespond);

        Mockito.when(userServices.getUserTicketsList()).thenReturn( getUserTicketsListResponds);

        mockMvc.perform(get("/services/ticket/my-tickets-list"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].ticketId").value(1L))
                .andExpect(jsonPath("$[0].ticketName").value(ticketName))
                .andExpect(jsonPath("$[0].menhgia").value(menhgia))
                .andExpect(jsonPath("$[0].expiryDate").value(expiryDate.toString()))
                .andExpect(jsonPath("$[0].issueDate").value(issueDate.toString()))
                .andExpect(jsonPath("$[0].status").value(status))
                .andExpect(jsonPath("$[0].qr_code").value(Base64.getEncoder().encodeToString(qrcode)));
    }

    @Feature("User Get Ticket Wallet")
    @Test
    @TmsLink("58")
    @DisplayName("Test Get My Tickets List Expired Token - Unauthenticated Failed")
    @WithAnonymousUser
    @Link(name = "TK-12" , type = "task")
    void testGetMyTicketsList_ExpiredToken_Failed() throws Exception {
        var ticketName = "VE NGAY";
        var menhgia = 9999;
        var expiryLocalDate = LocalDate.of(2025,12,31);
        Date expiryDate = Date.valueOf(expiryLocalDate);
        var issueLocalDate = LocalDate.of(2023,12,31);
        Date issueDate = Date.valueOf(issueLocalDate);
        var status = "DISABLED";
        byte[] qrcode = new byte[10];

        GetUserTicketsListRespond getUserTicketsListRespond = GetUserTicketsListRespond.builder()
                .ticketId(1L)
                .ticketName(ticketName)
                .menhgia(menhgia)
                .expiryDate(expiryDate)
                .issueDate(issueDate)
                .status(status)
                .qr_code(qrcode)
                .build();

        List<GetUserTicketsListRespond> getUserTicketsListResponds = Collections.singletonList(getUserTicketsListRespond);

        Mockito.when(userServices.getUserTicketsList()).thenReturn( getUserTicketsListResponds);

        mockMvc.perform(get("/services/ticket/my-tickets-list"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("code").value(1006))
                .andExpect(jsonPath("message").value("Unauthenticated"));
    }

    //Test for DELETE /services/ticket/my-tickets-list/{id}
    @Feature("User Delete Ticket By Id")
    @Test
    @TmsLink("59")
    @DisplayName("Test delete ticket only by User in User Ticket List - Delete Success")
    @WithMockUser(username = "test@gmail.com")
    @Link(name = "TK-13" , type = "task")
    void testDeleteTicketInUserTicketList_onlyByUser_Success() throws Exception {
        var id = 1L;

        Mockito.doNothing().when(userServices).UserDeleteTicket(any());

        objectMapper = new ObjectMapper();
        String content = objectMapper.writeValueAsString(id);

        mockMvc.perform(delete("/services/ticket/my-tickets-list/1")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(content))
                .andExpect(status().isOk());
    }

    @Feature("User Delete Ticket By Id")
    @Test
    @TmsLink("60")
    @DisplayName("Test delete ticket but expired token - Unauthenticated Failed")
    @WithAnonymousUser
    @Link(name = "TK-14" , type = "task")
    void testDeleteTicketInUserTicketList_ExpiredToken_Failed() throws Exception {
        var id = 1L;

        Mockito.doNothing().when(userServices).UserDeleteTicket(any());

        objectMapper = new ObjectMapper();
        String content = objectMapper.writeValueAsString(id);

        mockMvc.perform(delete("/services/ticket/my-tickets-list/1")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(content))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("code").value(1006))
                .andExpect(jsonPath("message").value("Unauthenticated"));
    }

    // Test for POST /services/ticket/my-tickets-list/enable-ticket/{id}
    @Feature("Enable Ticket By Id")
    @Test
    @TmsLink("61")
    @DisplayName("Test enable ticket with valid token - Enable Success")
    @WithMockUser(username = "test@gmail.com")
    @Link(name = "TK-15" , type = "task")
    void testEnableTicket_WithValidToken_EnableSuccess() throws Exception {
        EnableTicketRespond enableTicketRespond = EnableTicketRespond.builder()
                .status(true)
                .message("Enable ticket success")
                .ticketToken("abcxyz")
                .build();
        APIRespond<EnableTicketRespond> apiRespond = APIRespond.<EnableTicketRespond>builder()
                .result(enableTicketRespond)
                .build();

        var id = 1L;
        objectMapper = new ObjectMapper();
        String content = objectMapper.writeValueAsString(id);

        Mockito.when(userServices.enableTicket(any())).thenReturn(apiRespond.getResult());

        mockMvc.perform(post("/services/ticket/my-tickets-list/enable-ticket/1")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(content))
                .andExpect(status().isOk())
                .andExpect(jsonPath("result.status").value(true))
                .andExpect(jsonPath("result.message").value("Enable ticket success"))
                .andExpect(jsonPath("result.ticketToken").value("abcxyz"));
    }

    @Feature("Enable Ticket By Id")
    @Test
    @TmsLink("62")
    @DisplayName("Test enable ticket with expired token - Unauthenticated Failed")
    @WithAnonymousUser
    @Link(name = "TK-16" , type = "task")
    void testEnableTicket_WithExpiredToken_EnableFailed() throws Exception {
        EnableTicketRespond enableTicketRespond = EnableTicketRespond.builder()
                .status(true)
                .message("Enable ticket success")
                .ticketToken("abcxyz")
                .build();
        APIRespond<EnableTicketRespond> apiRespond = APIRespond.<EnableTicketRespond>builder()
                .result(enableTicketRespond)
                .build();

        var id = 1L;
        objectMapper = new ObjectMapper();
        String content = objectMapper.writeValueAsString(id);

        Mockito.when(userServices.enableTicket(any())).thenReturn(apiRespond.getResult());

        mockMvc.perform(post("/services/ticket/my-tickets-list/enable-ticket/1")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(content))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("code").value(1006))
                .andExpect(jsonPath("message").value("Unauthenticated"));
    }

    // Test for GET /services/ticket/all-user-tickets
    @Feature("Get All Sold Ticket")
    @Test
    @TmsLink("63")
    @DisplayName("Test get all user tickets list with Admin role - Get Success")
    @WithMockUser(username = "admin@gmail.com",roles = {"ADMIN"})
    @Link(name = "TK-17" , type = "task")
    void testGetAllUserTicketsList_AdminRole_Success() throws Exception {
        var expiryLocalDate = LocalDate.of(2025,12,31);
        Date expiryDate = Date.valueOf(expiryLocalDate);
        var issueLocalDate = LocalDate.of(2023,12,31);
        Date issueDate = Date.valueOf(issueLocalDate);
        var status = "DISABLED";

        GetAllUserTicketsListRespond getAllUserTicketsListRespond = GetAllUserTicketsListRespond.builder()
                .ticketId("VE01")
                .ticketName("VE NGAY")
                .MSSV("12345678")
                .email("test@gmail.com")
                .issueDate(issueDate)
                .expiryDate(expiryDate)
                .status(status)
                .menhgia(9999)
                .build();

        List<GetAllUserTicketsListRespond> getAllUserTicketsListRespondList = Collections
                .singletonList(getAllUserTicketsListRespond);

        Mockito.when(adminServices.getAllUserTickets()).thenReturn(getAllUserTicketsListRespondList);

        mockMvc.perform(get("/services/ticket/all-user-tickets"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].ticketId").value("VE01"))
                .andExpect(jsonPath("$[0].ticketName").value("VE NGAY"))
                .andExpect(jsonPath("$[0].mssv").value("12345678"))
                .andExpect(jsonPath("$[0].email").value("test@gmail.com"))
                .andExpect(jsonPath("$[0].issueDate").value(issueDate.toString()))
                .andExpect(jsonPath("$[0].expiryDate").value(expiryDate.toString()))
                .andExpect(jsonPath("$[0].status").value(status))
                .andExpect(jsonPath("$[0].menhgia").value(9999));
    }

    @Feature("Get All Sold Ticket")
    @Test
    @TmsLink("64")
    @DisplayName("Test get all user tickets list with User role - Forbidden Failed")
    @WithMockUser(username = "test@gmail.com")
    @Link(name = "TK-18" , type = "task")
    void testGetAllUserTicketsList_UserRole_Failed() throws Exception {
        var expiryLocalDate = LocalDate.of(2025,12,31);
        Date expiryDate = Date.valueOf(expiryLocalDate);
        var issueLocalDate = LocalDate.of(2023,12,31);
        Date issueDate = Date.valueOf(issueLocalDate);
        var status = "DISABLED";

        GetAllUserTicketsListRespond getAllUserTicketsListRespond = GetAllUserTicketsListRespond.builder()
                .ticketId("VE01")
                .ticketName("VE NGAY")
                .MSSV("12345678")
                .email("test@gmail.com")
                .issueDate(issueDate)
                .expiryDate(expiryDate)
                .status(status)
                .menhgia(9999)
                .build();

        List<GetAllUserTicketsListRespond> getAllUserTicketsListRespondList = Collections
                .singletonList(getAllUserTicketsListRespond);

        Mockito.when(adminServices.getAllUserTickets()).thenReturn(getAllUserTicketsListRespondList);

        mockMvc.perform(get("/services/ticket/all-user-tickets"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("code").value(1007))
                .andExpect(jsonPath("message").value("You do not have permission"));
    }

    @Feature("Get All Sold Ticket")
    @Test
    @TmsLink("65")
    @DisplayName("Test get all user tickets list expired token - Unauthenticated Failed")
    @WithAnonymousUser
    @Link(name = "TK-19" , type = "task")
    void testGetAllUserTicketsList_ExpiredToken_Failed() throws Exception {
        var expiryLocalDate = LocalDate.of(2025,12,31);
        Date expiryDate = Date.valueOf(expiryLocalDate);
        var issueLocalDate = LocalDate.of(2023,12,31);
        Date issueDate = Date.valueOf(issueLocalDate);
        var status = "DISABLED";

        GetAllUserTicketsListRespond getAllUserTicketsListRespond = GetAllUserTicketsListRespond.builder()
                .ticketId("VE01")
                .ticketName("VE NGAY")
                .MSSV("12345678")
                .email("test@gmail.com")
                .issueDate(issueDate)
                .expiryDate(expiryDate)
                .status(status)
                .menhgia(9999)
                .build();

        List<GetAllUserTicketsListRespond> getAllUserTicketsListRespondList = Collections
                .singletonList(getAllUserTicketsListRespond);

        Mockito.when(adminServices.getAllUserTickets()).thenReturn(getAllUserTicketsListRespondList);

        mockMvc.perform(get("/services/ticket/all-user-tickets"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("code").value(1006))
                .andExpect(jsonPath("message").value("Unauthenticated"));
    }

    // Test for GET /services/ticket/all-user-tickets/{MSSV}
    @Feature("Search Ticket Sold By MSSV")
    @Test
    @TmsLink("66")
    @DisplayName("Test search user tickets list with Admin role - Search Success")
    @WithMockUser(username = "admin@gmail.com",roles = {"ADMIN"})
    @Link(name = "TK-20" , type = "task")
    void testSearchUserTicketsList_AdminRole_Success() throws Exception {
        var expiryLocalDate = LocalDate.of(2025,12,31);
        Date expiryDate = Date.valueOf(expiryLocalDate);
        var issueLocalDate = LocalDate.of(2023,12,31);
        Date issueDate = Date.valueOf(issueLocalDate);
        var status = "DISABLED";
        var mssv = "12345678";

        GetAllUserTicketsListRespond getAllUserTicketsListRespond = GetAllUserTicketsListRespond.builder()
                .ticketId("VE01")
                .ticketName("VE NGAY")
                .MSSV("12345678")
                .email("test@gmail.com")
                .issueDate(issueDate)
                .expiryDate(expiryDate)
                .status(status)
                .menhgia(9999)
                .build();

        List<GetAllUserTicketsListRespond> getAllUserTicketsListRespondList = Collections
                .singletonList(getAllUserTicketsListRespond);

        objectMapper = new ObjectMapper();
        String content = objectMapper.writeValueAsString(mssv);

        Mockito.when(adminServices.findUserTicket(any())).thenReturn(getAllUserTicketsListRespondList);

        mockMvc.perform(get("/services/ticket/all-user-tickets/12345678")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(content))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].ticketId").value("VE01"))
                .andExpect(jsonPath("$[0].ticketName").value("VE NGAY"))
                .andExpect(jsonPath("$[0].mssv").value("12345678"))
                .andExpect(jsonPath("$[0].email").value("test@gmail.com"))
                .andExpect(jsonPath("$[0].issueDate").value(issueDate.toString()))
                .andExpect(jsonPath("$[0].expiryDate").value(expiryDate.toString()))
                .andExpect(jsonPath("$[0].status").value(status))
                .andExpect(jsonPath("$[0].menhgia").value(9999));
    }

    @Feature("Search Ticket Sold By MSSV")
    @Test
    @TmsLink("65")
    @DisplayName("Test get all user tickets list expired token - Unauthenticated Failed")
    @WithAnonymousUser
    @Link(name = "TK-21" , type = "task")
    void testSearchUserTicketsList_ExpiredToken_Failed() throws Exception {
        var expiryLocalDate = LocalDate.of(2025,12,31);
        Date expiryDate = Date.valueOf(expiryLocalDate);
        var issueLocalDate = LocalDate.of(2023,12,31);
        Date issueDate = Date.valueOf(issueLocalDate);
        var status = "DISABLED";
        var mssv = "12345678";

        GetAllUserTicketsListRespond getAllUserTicketsListRespond = GetAllUserTicketsListRespond.builder()
                .ticketId("VE01")
                .ticketName("VE NGAY")
                .MSSV("12345678")
                .email("test@gmail.com")
                .issueDate(issueDate)
                .expiryDate(expiryDate)
                .status(status)
                .menhgia(9999)
                .build();

        List<GetAllUserTicketsListRespond> getAllUserTicketsListRespondList = Collections
                .singletonList(getAllUserTicketsListRespond);

        objectMapper = new ObjectMapper();
        String content = objectMapper.writeValueAsString(mssv);

        Mockito.when(adminServices.findUserTicket(any())).thenReturn(getAllUserTicketsListRespondList);

        mockMvc.perform(get("/services/ticket/all-user-tickets/12345678")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(content))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("code").value(1006))
                .andExpect(jsonPath("message").value("Unauthenticated"));
    }

    // Test for DELETE /services/ticket/all-user-tickets/{id}
    @Feature("Delete Ticket Sold By Id")
    @Test
    @TmsLink("67")
    @DisplayName("Test delete user ticket only by Admin - Delete Success")
    @WithMockUser(username = "admin@gmail.com",roles = {"ADMIN"})
    @Link(name = "TK-22" , type = "task")
    void testDeleteUserTicket_onlyByAdmin_Success() throws Exception {
        var id = 1L;

        Mockito.doNothing().when(adminServices).AdminDeleteTicket(any());

        objectMapper = new ObjectMapper();
        String content = objectMapper.writeValueAsString(id);

        mockMvc.perform(delete("/services/ticket/all-user-tickets/1")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(content))
                .andExpect(status().isOk());
    }

    @Feature("Delete Ticket Sold By Id")
    @Test
    @TmsLink("68")
    @DisplayName("Test delete user ticket expired token - Unauthenticated Failed")
    @WithAnonymousUser
    @Link(name = "TK-23" , type = "task")
    void testDeleteUserTicket_ExpiredToken_Failed() throws Exception {
        var id = 1L;

        Mockito.doNothing().when(adminServices).AdminDeleteTicket(any());

        objectMapper = new ObjectMapper();
        String content = objectMapper.writeValueAsString(id);

        mockMvc.perform(delete("/services/ticket/all-user-tickets/1")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(content))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("code").value(1006))
                .andExpect(jsonPath("message").value("Unauthenticated"));
    }
}
