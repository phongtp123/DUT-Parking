package com.example.DUT_Parking.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Date;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class LogoutUsers {
    @Id
    Long id;
    @OneToOne
    @MapsId
    @JsonIgnore
    RegisteredUsers registeredUsers;

    String email;
    Date expiryDate;
}
