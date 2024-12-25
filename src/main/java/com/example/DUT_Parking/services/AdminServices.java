package com.example.DUT_Parking.services;

import com.example.DUT_Parking.DTO.TicketCreate;
import com.example.DUT_Parking.respond.*;

import java.util.List;

public interface AdminServices {
    List<GetProfileRespond> getAllUsersProfile();
    List<GetProfileRespond> SearchUserProfile (String MSSV);
    TicketRespond createTicket(TicketCreate request);
    List<GetTicketTypeList> getAllTickets();
    void deleteTicket(String ticketId);
    void AdminDeleteTicket(Long id);
    List<GetAllUserTicketsListRespond> getAllUserTickets();
    List<GetAllUserTicketsListRespond> findUserTicket (String MSSV);
    List<GetAllPassDataRespond> getAllPassData();
    void deleteAllPassData();
}
