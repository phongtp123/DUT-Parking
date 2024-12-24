package com.example.DUT_Parking.controller;

import com.example.DUT_Parking.DTO.RechargeRequest;
import com.example.DUT_Parking.respond.APIRespond;
import com.example.DUT_Parking.respond.RechargeRespond;
import com.example.DUT_Parking.services.UserServices;
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
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Recharge Controller Test")
public class RechargeControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    @Qualifier("rechargeImpl")
    private UserServices userServices;

    @Autowired
    ObjectMapper objectMapper;

    //Test for POST /services/recharge
    @Feature("Recharge")
    @Test
    @TmsLink("45")
    @DisplayName("All Clear - Recharge Success")
    @WithMockUser(username = "test@gmail.com")
    @Link(name = "RECHARGE-01" , type = "task")
    void testRecharge_Success() throws Exception {
        var menhgia = 100000;

        RechargeRequest rechargeRequest = RechargeRequest.builder()
                .menhgia(menhgia)
                .build();

        RechargeRespond rechargeRespond = RechargeRespond.builder()
                .success(true)
                .message(String.format("Nạp thành công %s đồng", menhgia))
                .build();

        APIRespond<RechargeRespond> apiRespond = APIRespond.<RechargeRespond>builder()
                .result(rechargeRespond)
                .build();

        objectMapper = new ObjectMapper();
        String content = objectMapper.writeValueAsString(rechargeRequest);

        Mockito.when(userServices.recharge(any())).thenReturn(apiRespond.getResult());

        mockMvc.perform(post("/services/recharge")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(content))
                .andExpect(status().isOk())
                .andExpect(jsonPath("result.success").value(true))
                .andExpect(jsonPath("result.message")
                        .value(String.format("Nạp thành công %s đồng",menhgia)));
    }

    @Feature("Recharge")
    @Test
    @TmsLink("46")
    @DisplayName("Test Recharge With Token Expired - Unauthenticated Failed")
    @WithAnonymousUser
    @Link(name = "RECHARGE-01" , type = "task")
    void testRecharge_Failed() throws Exception {
        var menhgia = 100000;

        RechargeRequest rechargeRequest = RechargeRequest.builder()
                .menhgia(menhgia)
                .build();

        RechargeRespond rechargeRespond = RechargeRespond.builder()
                .success(true)
                .message(String.format("Nạp thành công %s đồng", menhgia))
                .build();

        APIRespond<RechargeRespond> apiRespond = APIRespond.<RechargeRespond>builder()
                .result(rechargeRespond)
                .build();

        objectMapper = new ObjectMapper();
        String content = objectMapper.writeValueAsString(rechargeRequest);

        Mockito.when(userServices.recharge(any())).thenReturn(apiRespond.getResult());

        mockMvc.perform(post("/services/recharge")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(content))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("code").value(1006))
                .andExpect(jsonPath("message")
                        .value("Unauthenticated"));
    }
}
