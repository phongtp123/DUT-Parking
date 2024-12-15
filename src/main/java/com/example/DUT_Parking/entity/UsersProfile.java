package com.example.DUT_Parking.entity;


import com.example.DUT_Parking.repository.TicketsRepo;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
@Entity
public class UsersProfile {
    @Id
    String MSSV;
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
    @OneToMany(mappedBy = "usersProfile" , cascade = CascadeType.ALL)
    List<UserTicketsInfo> userTicketsInfos = new ArrayList<>();
    @OneToMany(mappedBy = "usersProfile" , cascade = CascadeType.ALL)
    List<PassMonitor> passMonitors = new ArrayList<>();

}
