package com.example.DUT_Parking.services;

import com.example.DUT_Parking.DTO.TicketCreate;
import com.example.DUT_Parking.entity.PassMonitor;
import com.example.DUT_Parking.entity.UserTicketsInfo;
import com.example.DUT_Parking.entity.UsersProfile;
import com.example.DUT_Parking.respond.*;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

public interface AdminServices {
    @PreAuthorize("hasRole('ADMIN')")
    void deleteUserProfile (String MSSV);
    @PreAuthorize("hasRole('ADMIN')")
    List<GetProfileRespond> getAllUsersProfile();
    @PreAuthorize("hasRole('ADMIN')")
    List<GetProfileRespond> SearchUserProfile (String MSSV);
    @PreAuthorize("hasRole('ADMIN')")
    TicketRespond createTicket(TicketCreate request);
    @PreAuthorize("hasRole('ADMIN')")
    List<GetTicketTypeList> getAllTickets();
    @PreAuthorize("hasRole('ADMIN')")
    void deleteTicket(String ticketId);
    @PreAuthorize("hasRole('ADMIN')")
    void AdminDeleteTicket(Long id);
    @PreAuthorize("hasRole('ADMIN')")
    List<GetAllUserTicketsListRespond> getAllUserTickets();
    @PreAuthorize("hasRole('ADMIN')")
    List<GetAllUserTicketsListRespond> findUserTicket (String MSSV);
    @PreAuthorize("hasRole('ADMIN')")
    List<GetAllPassDataRespond> getAllPassData();
    @PreAuthorize("hasRole('ADMIN')")
    void deleteAllPassData();
}
