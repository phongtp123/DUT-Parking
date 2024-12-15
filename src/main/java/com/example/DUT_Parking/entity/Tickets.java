package com.example.DUT_Parking.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
@Entity
public class Tickets {
    @Id
    String ticketId;
    String ticketName;
    int menhgia;
    @OneToMany(mappedBy = "tickets" , cascade = CascadeType.ALL)
    List<UserTicketsInfo> userTicketsInfos = new ArrayList<>();
}
