package com.example.DUT_Parking.respond;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.sql.Date;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class GetProfileRespond {
    String MSSV;
    String email;
    String hovaten;
    String gioitinh;
    Date dob;
    String diachi;
    String quequan;
    String sdt;
    long sodu;
}
