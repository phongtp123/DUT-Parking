package com.example.DUT_Parking.Controller;

import com.example.DUT_Parking.DTO.PassRequest;
import com.example.DUT_Parking.entity.PassMonitor;
import com.example.DUT_Parking.respond.APIRespond;
import com.example.DUT_Parking.respond.GetAllPassDataRespond;
import com.example.DUT_Parking.services.AdminServices;
import com.example.DUT_Parking.services.PassMonitorService;
import com.example.DUT_Parking.services.UserServices;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.util.List;

@RestController
@RequestMapping("/monitor")
@FieldDefaults(level = AccessLevel.PRIVATE , makeFinal = true)
public class PassMonitorController {
    PassMonitorService passMonitorService;
    AdminServices adminServices;

    public PassMonitorController(@Qualifier("passMonitorImpl") PassMonitorService passMonitorService,@Qualifier("passMonitorImpl") AdminServices adminServices) {
        this.adminServices = adminServices;
        this.passMonitorService = passMonitorService;
    }

    @PostMapping
    APIRespond<Void> HandlePassData(@RequestBody PassRequest request) throws ParseException {
        passMonitorService.HandlePassData(request);
        return APIRespond.<Void>builder()
                .build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/pass-monitor")
    List<GetAllPassDataRespond> getAllPassData() throws ParseException {
        return adminServices.getAllPassData();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/pass-monitor/reset")
    APIRespond<Void> deletePassData() {
        adminServices.deleteAllPassData();
        return APIRespond.<Void>builder()
                .build();
    }
}
