package com.example.DUT_Parking.respond;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RechargeRespond {
    boolean success;
    String message;
}
