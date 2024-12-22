package com.example.DUT_Parking.services;

import com.example.DUT_Parking.DTO.RechargeRequest;
import com.example.DUT_Parking.configuration.SecurityHolder;
import com.example.DUT_Parking.entity.UsersProfile;
import com.example.DUT_Parking.repository.UsersProfileRepo;
import com.example.DUT_Parking.respond.RechargeRespond;
import com.example.DUT_Parking.services.impl.RechargeImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;

@ExtendWith(MockitoExtension.class)
public class RechargeServiceTest {
    @InjectMocks
    public RechargeImpl rechargeImpl;

    @Mock
    private UsersProfileRepo usersProfileRepo;

    @Mock
    private SecurityHolder securityHolder;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    // Test for function recharge()
    @Test
    @DisplayName("Test recharge function - Recharge success")
    public void testRecharge_Success() {
        RechargeImpl spy = Mockito.spy(rechargeImpl);
        String name = "test@gmail.com";
        UsersProfile usersProfile = new UsersProfile();

        RechargeRequest request = RechargeRequest.builder()
                .menhgia(9999)
                .build();

        RechargeRespond rechargeRespond = RechargeRespond.builder()
                .success(true)
                .message(String.format("Nạp thành công %s đồng" , request.getMenhgia()))
                .build();

        Mockito.doReturn(securityContext).when(securityHolder).getContext();
        Mockito.doReturn(authentication).when(securityContext).getAuthentication();
        Mockito.doReturn(name).when(authentication).getName();
        Mockito.when(usersProfileRepo.findByEmail(name))
                .thenReturn(usersProfile);

        var respond = spy.recharge(request);

        Assertions.assertEquals(rechargeRespond, respond);
        Assertions.assertEquals(9999, usersProfile.getSodu());

        Mockito.verify(usersProfileRepo).save(usersProfile);
    }

}
