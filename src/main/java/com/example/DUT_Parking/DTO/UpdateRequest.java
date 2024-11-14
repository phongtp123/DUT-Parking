package com.example.DUT_Parking.DTO;

import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdateRequest {
    @NotBlank(message = "INVALID_NAME")
    String hovaten;
    @NotBlank(message = "INVALID_SDT")
    String sdt;
    @NotBlank(message = "INVALID_DIACHI")
    String diachi;
    @NotBlank(message = "INVALID_QUEQUAN")
    String quequan;
    @NotBlank(message = "INVALID_GENDER")
    String gioitinh;
    LocalDate dob;

}
