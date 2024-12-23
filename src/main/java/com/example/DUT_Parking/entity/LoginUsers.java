package com.example.DUT_Parking.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.apache.catalina.User;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class LoginUsers {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    @OneToOne
    @MapsId
    @JsonIgnore
    RegisteredUsers registeredUsers;

    @Column(nullable = false, unique = true)
    String email;
}
