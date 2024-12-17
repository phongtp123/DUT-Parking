package com.example.DUT_Parking.respond;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class GetLogoutUsers {
    Long id;
    String email;
    Date expiryDate;
}
