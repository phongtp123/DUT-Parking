package com.example.DUT_Parking.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
@Entity
public class UserTicketsInfo {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne
    UsersProfile usersProfile;

    @ManyToOne
    Tickets tickets;

    Date issueDate;
    Date expiryDate;
    String status;
    @Lob @Column(name = "qr_code" , columnDefinition = "longblob")
    byte[] qr_code;

    @OneToMany(mappedBy = "userTicketsInfo" , cascade = CascadeType.ALL)
    List<PassMonitor> passMonitors = new ArrayList<>();
}
