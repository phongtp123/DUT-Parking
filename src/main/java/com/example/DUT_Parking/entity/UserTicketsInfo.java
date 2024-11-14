package com.example.DUT_Parking.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Date;
import java.util.List;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
@Entity
public class UserTicketsInfo {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    String email;
    String ticketName;
    Date issueDate;
    Date expiryDate;
    int menhgia;
    String status;
}
