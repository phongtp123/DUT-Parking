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
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AuthenticationRequest {
    @NotNull(message = "EMAIL_INVALID")
    @Pattern(regexp = "^[a-zA-Z0-9]+@gmail\\.com$" , message = "EMAIL_INVALID")
    String email;
    @NotNull(message = "INVALID_PASSWORD")
    String password;
}
