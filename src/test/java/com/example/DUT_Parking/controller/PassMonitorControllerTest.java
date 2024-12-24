package com.example.DUT_Parking.controller;

import com.example.DUT_Parking.DTO.RegisterRequest;
import com.example.DUT_Parking.respond.GetAllPassDataRespond;
import com.example.DUT_Parking.respond.GetRegisteredUsers;
import com.example.DUT_Parking.services.AdminServices;
import com.example.DUT_Parking.services.PassMonitorService;
import com.example.DUT_Parking.services.RegisterService;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.qameta.allure.Feature;
import io.qameta.allure.Link;
import io.qameta.allure.TmsLink;
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

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Pass Monitor Controller Test")
public class PassMonitorControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    @Qualifier("passMonitorImpl")
    private AdminServices adminServices;

    @MockBean
    @Qualifier("passMonitorImpl")
    private PassMonitorService passMonitorService;

    // Test for GET /monitor/pass-monitor
    @Feature("Open Pass Monitor")
    @Test
    @TmsLink("69")
    @DisplayName("Test open pass monitor with Admin role - Open Success")
    @WithMockUser(username = "admin@gmail.com" , roles = {"ADMIN"})
    @Link(name = "PM-01" , type = "task")
    void testOpenPassMonitor_WithAdminRole_Success() throws Exception {
        GetAllPassDataRespond getAllPassDataRespond = GetAllPassDataRespond.builder()
                .id(1L)
                .ticketName("VE NGAY")
                .hovaten("Nguyễn Thanh Phong")
                .email("test@gmail.com")
                .decision("PASS")
                .build();

        List<GetAllPassDataRespond> getAllPassDataRespondList = Collections.singletonList(getAllPassDataRespond);

        Mockito.when(adminServices.getAllPassData()).thenReturn(getAllPassDataRespondList);

        mockMvc.perform(get("/monitor/pass-monitor"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].ticketName").value("VE NGAY"))
                .andExpect(jsonPath("$[0].hovaten").value("Nguyễn Thanh Phong"))
                .andExpect(jsonPath("$[0].email").value("test@gmail.com"))
                .andExpect(jsonPath("$[0].decision").value("PASS"));
    }

    @Feature("Open Pass Monitor")
    @Test
    @TmsLink("70")
    @DisplayName("Test open pass monitor with User role - Forbidden Failed")
    @WithMockUser(username = "test@gmail.com")
    @Link(name = "PM-02" , type = "task")
    void testOpenPassMonitor_WithUserRole_Failed() throws Exception {
        GetAllPassDataRespond getAllPassDataRespond = GetAllPassDataRespond.builder()
                .id(1L)
                .ticketName("VE NGAY")
                .hovaten("Nguyễn Thanh Phong")
                .email("test@gmail.com")
                .decision("PASS")
                .build();

        List<GetAllPassDataRespond> getAllPassDataRespondList = Collections.singletonList(getAllPassDataRespond);

        Mockito.when(adminServices.getAllPassData()).thenReturn(getAllPassDataRespondList);

        mockMvc.perform(get("/monitor/pass-monitor"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("code").value(1007))
                .andExpect(jsonPath("message").value("You do not have permission"));
    }

    @Feature("Open Pass Monitor")
    @Test
    @TmsLink("71")
    @DisplayName("Test open pass monitor expired token - Unauthenticated Failed")
    @WithAnonymousUser
    @Link(name = "PM-03" , type = "task")
    void testOpenPassMonitor_ExpiredToken_Failed() throws Exception {
        GetAllPassDataRespond getAllPassDataRespond = GetAllPassDataRespond.builder()
                .id(1L)
                .ticketName("VE NGAY")
                .hovaten("Nguyễn Thanh Phong")
                .email("test@gmail.com")
                .decision("PASS")
                .build();

        List<GetAllPassDataRespond> getAllPassDataRespondList = Collections.singletonList(getAllPassDataRespond);

        Mockito.when(adminServices.getAllPassData()).thenReturn(getAllPassDataRespondList);

        mockMvc.perform(get("/monitor/pass-monitor"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("code").value(1006))
                .andExpect(jsonPath("message").value("Unauthenticated"));
    }

    //Test DELETE /monitor/pass-monitor/reset
    @Feature("Reset Pass Monitor")
    @Test
    @TmsLink("72")
    @DisplayName("Test delete all data in pass monitor - Reset Success")
    @WithMockUser(username = "admin@gmail.com" , roles = {"ADMIN"})
    @Link(name = "PM-04" , type = "task")
    void testReset_Success() throws Exception {

        Mockito.doNothing().when(adminServices).deleteAllPassData();

        mockMvc.perform(delete("/monitor/pass-monitor/reset"))
                .andExpect(status().isOk());
    }

    @Feature("Reset Pass Monitor")
    @Test
    @TmsLink("73")
    @DisplayName("Test delete all data expired token - Unauthenticated Failed")
    @WithAnonymousUser
    @Link(name = "PM-05" , type = "task")
    void testReset_ExpiredToken_Failed() throws Exception {

        Mockito.doNothing().when(adminServices).deleteAllPassData();

        mockMvc.perform(delete("/monitor/pass-monitor/reset"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("code").value(1006))
                .andExpect(jsonPath("message").value("Unauthenticated"));
    }
}
