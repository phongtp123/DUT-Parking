package com.example.DUT_Parking.services;

import com.example.DUT_Parking.DTO.TicketRequest;
import com.example.DUT_Parking.entity.PassMonitor;
import com.example.DUT_Parking.entity.UserTicketsInfo;
import com.example.DUT_Parking.entity.UsersProfile;
import com.example.DUT_Parking.respond.GetProfileRespond;
import com.example.DUT_Parking.respond.GetUserTicketsListRespond;
import com.example.DUT_Parking.respond.TicketRespond;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

public interface AdminServices {
    @PreAuthorize("hasRole('ADMIN')")
    void deleteUserProfile (Long id);
    @PreAuthorize("hasRole('ADMIN')")
    List<UsersProfile> getAllUsersProfile();
    @PreAuthorize("hasRole('ADMIN')")
    GetProfileRespond SearchUserProfile (String hovaten);
    @PreAuthorize("hasRole('ADMIN')")
    TicketRespond createTicket(TicketRequest request);
    @PreAuthorize("hasRole('ADMIN')")
    List<TicketRespond> getAllTickets();
    @PreAuthorize("hasRole('ADMIN')")
    void deleteTicket(String ticket_name);
    @PreAuthorize("hasRole('ADMIN')")
    void AdminDeleteTicket(Long id);
    @PreAuthorize("hasRole('ADMIN')")
    List<UserTicketsInfo> getAllUserTickets();
    @PreAuthorize("hasRole('ADMIN')")
    List<GetUserTicketsListRespond> findUserTicket (String email);
    @PreAuthorize("hasRole('ADMIN')")
    List<PassMonitor> getAllPassData();
    @PreAuthorize("hasRole('ADMIN')")
    void deleteAllPassData();
}
