package com.example.DUT_Parking.services;

import com.example.DUT_Parking.DTO.BuyTicketRequest;
import com.example.DUT_Parking.DTO.RechargeRequest;
import com.example.DUT_Parking.DTO.UpdateRequest;
import com.example.DUT_Parking.respond.*;

import java.text.ParseException;
import java.util.List;


public interface UserServices {
    UpdateRespond UpdateProfile(UpdateRequest user_profile);
    GetProfileRespond GetUserProfile ();
    RechargeRespond recharge (RechargeRequest request);
    BuyTicketRespond buyTicket(BuyTicketRequest request);
    List<GetUserTicketsListRespond> getUserTicketsList();
    EnableTicketRespond enableTicket(Long id) throws ParseException;
    void UserDeleteTicket(Long id);

}
