package com.example.DUT_Parking.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Date;

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
    @Lob @Column(name = "qr_code" , columnDefinition = "longblob")
    byte[] qr_code;
}
