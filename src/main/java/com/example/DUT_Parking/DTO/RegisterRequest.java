package com.example.DUT_Parking.DTO;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RegisterRequest {
    @Email(message ="EMAIL_INVALID")
    String email;
    @Size(min = 6, max = 20, message = "INVALID_PASSWORD")
    String password;
}
