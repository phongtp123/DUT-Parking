package com.example.DUT_Parking.respond;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BuyTicketRespond {
    boolean status;
    String message;
}
