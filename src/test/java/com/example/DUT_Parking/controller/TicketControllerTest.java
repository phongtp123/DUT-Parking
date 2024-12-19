package com.example.DUT_Parking.controller;

import com.example.DUT_Parking.DTO.BuyTicketRequest;
import com.example.DUT_Parking.DTO.RegisterRequest;
import com.example.DUT_Parking.DTO.TicketCreate;
import com.example.DUT_Parking.respond.*;
import com.example.DUT_Parking.services.AdminServices;
import com.example.DUT_Parking.services.RegisterService;
import com.example.DUT_Parking.services.UserServices;
import com.fasterxml.jackson.databind.ObjectMapper;
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
    @Test
    @DisplayName("Test Create Ticket With Admin Role - Create Success")
    @WithMockUser(username = "admin@gmail.com" , roles = {"ADMIN"})
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

    @Test
    @DisplayName("Test Create Ticket With User Role - Forbidden Failed")
    @WithMockUser(username = "test@gmail.com")
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

    @Test
    @DisplayName("Test Create Ticket Expired Token - Unauthenticated Failed")
    @WithAnonymousUser
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
    @Test
    @DisplayName("Test Get Ticket Type List With Admin Role - Get Success")
    @WithMockUser(username = "admin@gmail.com" , roles = {"ADMIN"})
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

    @Test
    @DisplayName("Test Get Ticket Type List With User Role - Forbidden Failed")
    @WithMockUser(username = "test@gmail.com")
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

    @Test
    @DisplayName("Test Get Ticket Type List Expired Token - Unauthenticated Failed")
    @WithAnonymousUser
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
    @Test
    @DisplayName("Test Delete Ticket Type List With Admin Role - Delete Success")
    @WithMockUser(username = "admin@gmail.com" , roles = {"ADMIN"})
    void testDeleteTicketType_AdminRole_Success() throws Exception {
        var ticketId = "VE01";

        Mockito.doNothing().when(adminServices).deleteTicket(any());

        mockMvc.perform(delete("/services/ticket/tickets-list/VE01"))
                .andExpect(status().isOk())
                .andExpect(content()
                        .string(String.format("Ticket %s has been delete successfully", ticketId)));
    }

    @Test
    @DisplayName("Test Delete Ticket Type List Expired Token - Unauthenticated Failed")
    @WithAnonymousUser
    void testDeleteTicketType_ExpiredToken_Failed() throws Exception {

        Mockito.doNothing().when(adminServices).deleteTicket(any());

        mockMvc.perform(delete("/services/ticket/tickets-list/VE01"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("code").value(1006))
                .andExpect(jsonPath("message").value("Unauthenticated"));
    }

    //Test for POST /services/ticket/buy-ticket
    @Test
    @DisplayName("Test Buy Ticket With Valid Token - Buy Success")
    @WithMockUser(username = "test@gmail.com")
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

    @Test
    @DisplayName("Test Buy Ticket With Expired Token - Unauthenticated Failed")
    @WithAnonymousUser
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
    @Test
    @DisplayName("Test Get My Tickets List - Get Success")
    @WithMockUser(username = "test@gmail.com")
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

    @Test
    @DisplayName("Test Get My Tickets List Expired Token - Unauthenticated Failed")
    @WithAnonymousUser
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
    @Test
    @DisplayName("Test delete ticket only by User in User Ticket List - Delete Success")
    @WithMockUser(username = "test@gmail.com")
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

    @Test
    @DisplayName("Test delete ticket but expired token - Unauthenticated Failed")
    @WithAnonymousUser
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
    @Test
    @DisplayName("Test enable ticket with valid token - Enable Success")
    @WithMockUser(username = "test@gmail.com")
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

    @Test
    @DisplayName("Test enable ticket with expired token - Unauthenticated Failed")
    @WithAnonymousUser
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
    @Test
    @DisplayName("Test get all user tickets list with Admin role - Get Success")
    @WithMockUser(username = "admin@gmail.com",roles = {"ADMIN"})
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

    @Test
    @DisplayName("Test get all user tickets list with User role - Forbidden Failed")
    @WithMockUser(username = "test@gmail.com")
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

    @Test
    @DisplayName("Test get all user tickets list expired token - Unauthenticated Failed")
    @WithAnonymousUser
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
    @Test
    @DisplayName("Test search user tickets list with Admin role - Search Success")
    @WithMockUser(username = "admin@gmail.com",roles = {"ADMIN"})
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

    @Test
    @DisplayName("Test get all user tickets list expired token - Unauthenticated Failed")
    @WithAnonymousUser
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
    @Test
    @DisplayName("Test delete user ticket only by Admin - Delete Success")
    @WithMockUser(username = "admin@gmail.com",roles = {"ADMIN"})
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

    @Test
    @DisplayName("Test delete user ticket expired token - Unauthenticated Failed")
    @WithAnonymousUser
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
