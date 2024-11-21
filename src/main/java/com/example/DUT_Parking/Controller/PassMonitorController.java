package com.example.DUT_Parking.Controller;

import com.example.DUT_Parking.DTO.PassRequest;
import com.example.DUT_Parking.entity.PassMonitor;
import com.example.DUT_Parking.respond.APIRespond;
import com.example.DUT_Parking.services.PassMonitorService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.util.List;

@RestController
@RequestMapping("/monitor")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE , makeFinal = true)
public class PassMonitorController {
    PassMonitorService passMonitorService;

    @PostMapping
    APIRespond<Void> HandlePassData(@RequestBody PassRequest request) throws ParseException {
        passMonitorService.HandlePassData(request);
        return APIRespond.<Void>builder()
                .build();
    }

    @GetMapping
    APIRespond<List<PassMonitor>> getAllPassData() throws ParseException {
        return APIRespond.<List<PassMonitor>>builder()
                .result(passMonitorService.getAllPassData())
                .build();
    }
}
