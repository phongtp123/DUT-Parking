package com.example.DUT_Parking.respond;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class GetProfileRespond {
    Long id;
    String email;
    String hovaten;
    String gioitinh;
    LocalDate dob;
    String diachi;
    String quequan;
    String sdt;
    long sodu;
    Set<String> roles;
}
