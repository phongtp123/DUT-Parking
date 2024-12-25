package com.example.DUT_Parking.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
@Entity
public class PassMonitor {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne
    UsersProfile usersProfile;

    @ManyToOne
    UserTicketsInfo userTicketsInfo;

    String decision;

    LocalDate passTime;
}
