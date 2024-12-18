package com.example.DUT_Parking.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.sql.Date;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class UpdateRequest {
    String MSSV;
    @NotNull(message = "INVALID_NAME")
    @NotBlank(message = "INVALID_NAME")
    String hovaten;
    @NotNull(message = "INVALID_SDT")
    @NotBlank(message = "INVALID_NAME")
    String sdt;
    @NotNull(message = "INVALID_DIACHI")
    @NotBlank(message = "INVALID_NAME")
    String diachi;
    @NotNull(message = "INVALID_QUEQUAN")
    @NotBlank(message = "INVALID_NAME")
    String quequan;
    @NotNull(message = "INVALID_GENDER")
    @NotBlank(message = "INVALID_NAME")
    String gioitinh;
    Date dob;

}
