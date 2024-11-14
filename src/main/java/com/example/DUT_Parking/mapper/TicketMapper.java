package com.example.DUT_Parking.mapper;

import com.example.DUT_Parking.DTO.TicketRequest;
import com.example.DUT_Parking.entity.Tickets;
import com.example.DUT_Parking.respond.TicketRespond;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TicketMapper {
    Tickets toTicket(TicketRequest request);
    TicketRespond toTicketRespond(Tickets tickets);
}
