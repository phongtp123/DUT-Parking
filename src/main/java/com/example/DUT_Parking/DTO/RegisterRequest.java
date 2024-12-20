package com.example.DUT_Parking.DTO;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class RegisterRequest {
    @NotNull(message = "EMAIL_INVALID")
    @Pattern(regexp = "^[a-zA-Z0-9]+@gmail\\.com$" , message = "EMAIL_INVALID")
    String email;
    @NotNull(message = "INVALID_PASSWORD")
    @Size(min = 6, max = 20, message = "INVALID_PASSWORD")
    String password;
}
