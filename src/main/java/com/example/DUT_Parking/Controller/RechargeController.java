package com.example.DUT_Parking.Controller;

import com.example.DUT_Parking.DTO.RechargeRequest;
import com.example.DUT_Parking.respond.APIRespond;
import com.example.DUT_Parking.respond.RechargeRespond;
import com.example.DUT_Parking.services.UserServices;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/services")
@FieldDefaults(level = AccessLevel.PRIVATE , makeFinal = true)
public class RechargeController {

    private final UserServices userServices;

    public RechargeController(@Qualifier("rechargeImpl") UserServices userServices) {
        this.userServices = userServices;
    }

    @PostMapping("/recharge")
    APIRespond <RechargeRespond> recharge (@RequestBody RechargeRequest request){
        var result = userServices.recharge(request);
        return  APIRespond.<RechargeRespond>builder()
                .result(result)
                .build();
    }
}
