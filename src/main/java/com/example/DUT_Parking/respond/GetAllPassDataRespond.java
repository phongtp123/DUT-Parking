package com.example.DUT_Parking.respond;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class GetAllPassDataRespond {
    Long id;
    String hovaten;
    String email;
    String ticketName;
    String decision;
    LocalDate passTime;
}
