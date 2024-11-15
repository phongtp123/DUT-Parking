package com.example.DUT_Parking.entity;


import com.example.DUT_Parking.repository.TicketsRepo;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
@Entity
public class UsersProfile {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    String email;
    String password;
    String hovaten;
    String gioitinh;
    LocalDate dob;
    String diachi;
    String quequan;
    String sdt;
    long sodu = 0;
    @ElementCollection
    Set<String> roles;

    public boolean isEmpty() {
        return email == null;
    }
}
