package com.example.DUT_Parking.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
@Entity
public class PassMonitor {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    String hovaten;
    String email;
    String ticketName;
    String decision;
}