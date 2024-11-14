package com.example.DUT_Parking.respond;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class GetUserTicketsListRespond {
    Long ticketId;
    String ticketName;
    Date issueDate;
    Date expiryDate;
    int menhgia;
    String status;
}
