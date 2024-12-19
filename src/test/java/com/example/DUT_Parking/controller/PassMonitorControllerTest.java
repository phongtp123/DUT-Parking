package com.example.DUT_Parking.controller;

import com.example.DUT_Parking.DTO.RegisterRequest;
import com.example.DUT_Parking.respond.GetAllPassDataRespond;
import com.example.DUT_Parking.respond.GetRegisteredUsers;
import com.example.DUT_Parking.services.AdminServices;
import com.example.DUT_Parking.services.PassMonitorService;
import com.example.DUT_Parking.services.RegisterService;
import com.fasterxml.jackson.databind.ObjectMapper;
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
    @Test
    @DisplayName("Test open pass monitor with Admin role - Open Success")
    @WithMockUser(username = "admin@gmail.com" , roles = {"ADMIN"})
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

    @Test
    @DisplayName("Test open pass monitor with User role - Forbidden Failed")
    @WithMockUser(username = "test@gmail.com")
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

    @Test
    @DisplayName("Test open pass monitor expired token - Unauthenticated Failed")
    @WithAnonymousUser
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
    @Test
    @DisplayName("Test delete all data in pass monitor - Reset Success")
    @WithMockUser(username = "admin@gmail.com" , roles = {"ADMIN"})
    void testReset_Success() throws Exception {

        Mockito.doNothing().when(adminServices).deleteAllPassData();

        mockMvc.perform(delete("/monitor/pass-monitor/reset"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Test delete all data expired token - Unauthenticated Failed")
    @WithAnonymousUser
    void testReset_ExpiredToken_Failed() throws Exception {

        Mockito.doNothing().when(adminServices).deleteAllPassData();

        mockMvc.perform(delete("/monitor/pass-monitor/reset"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("code").value(1006))
                .andExpect(jsonPath("message").value("Unauthenticated"));
    }
}
