package com.example.DUT_Parking.entity;

import jakarta.persistence.*;
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
    @Column(name = "email" , unique = true , columnDefinition = "VARCHAR(255) COLLATE utf8mb4_unicode_ci")
    String email;
    @Column(nullable = false)
    String password;
    @OneToOne(mappedBy = "registeredUsers", cascade = CascadeType.ALL)
    LoginUsers loginUsers;
    @OneToOne(mappedBy = "registeredUsers", cascade = CascadeType.ALL)
    LogoutUsers logoutUsers;
}
