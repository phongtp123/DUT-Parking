package com.example.DUT_Parking.services;

import com.example.DUT_Parking.DTO.BuyTicketRequest;
import com.example.DUT_Parking.DTO.EnableTicketRequest;
import com.example.DUT_Parking.DTO.RechargeRequest;
import com.example.DUT_Parking.DTO.UpdateRequest;
import com.example.DUT_Parking.respond.*;
import org.springframework.security.access.prepost.PreAuthorize;

import java.text.ParseException;
import java.util.List;


public interface UserServices {
    @PreAuthorize("hasRole('USER')")
    UpdateRespond UpdateProfile(UpdateRequest user_profile);
    @PreAuthorize("hasRole('USER')")
    GetProfileRespond GetUserProfile ();
    @PreAuthorize("hasRole('USER')")
    RechargeRespond recharge (RechargeRequest request);
    @PreAuthorize("hasRole('USER')")
    BuyTicketRespond buyTicket(BuyTicketRequest request);
    @PreAuthorize("hasRole('USER')")
    List<GetUserTicketsListRespond> getUserTicketsList();
    @PreAuthorize("hasRole('USER')")
    EnableTicketRespond enableTicket(Long id) throws ParseException;
    @PreAuthorize("hasRole('USER')")
    void UserDeleteTicket(Long id);

}
