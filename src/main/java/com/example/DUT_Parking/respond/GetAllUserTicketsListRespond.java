package com.example.DUT_Parking.respond;

import jakarta.persistence.Lob;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class GetAllUserTicketsListRespond {
    Long ticketId;
    String email;
    String MSSV;
    String ticketName;
    Date issueDate;
    Date expiryDate;
    int menhgia;
    String status;
}
