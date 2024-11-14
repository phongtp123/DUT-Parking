package com.example.DUT_Parking.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class RegisteredUsers {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    String email;
    String password;

}
