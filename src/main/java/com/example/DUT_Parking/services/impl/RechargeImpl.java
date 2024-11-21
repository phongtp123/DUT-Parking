package com.example.DUT_Parking.services.impl;

import com.example.DUT_Parking.DTO.BuyTicketRequest;
import com.example.DUT_Parking.DTO.EnableTicketRequest;
import com.example.DUT_Parking.DTO.RechargeRequest;
import com.example.DUT_Parking.DTO.UpdateRequest;
import com.example.DUT_Parking.entity.UsersProfile;
import com.example.DUT_Parking.repository.UsersProfileRepo;
import com.example.DUT_Parking.respond.*;
import com.example.DUT_Parking.services.UserServices;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.List;

@Service("rechargeImpl")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE , makeFinal = true)
public class RechargeImpl implements UserServices {
    UsersProfileRepo usersProfileRepo;

    @Override
    public UpdateRespond UpdateProfile(UpdateRequest user_profile) {
        return null;
    }

    @Override
    public GetProfileRespond GetUserProfile() {
        return null;
    }

    public RechargeRespond recharge(RechargeRequest request) {
        var info = SecurityContextHolder.getContext();
        String name = info.getAuthentication().getName();
        UsersProfile usersProfile = usersProfileRepo.findByEmail(name);
        var last_sodu = usersProfile.getSodu();
        var latest_sodu = last_sodu + request.getMenhgia();
        usersProfile.setSodu(latest_sodu);
        usersProfileRepo.save(usersProfile);
        return RechargeRespond.builder()
                .success(true)
                .message(String.format("Nạp thành công %s đồng" , request.getMenhgia()))
                .build();
    }

    @Override
    public BuyTicketRespond buyTicket(BuyTicketRequest request) {
        return null;
    }

    @Override
    public List<GetUserTicketsListRespond> getUserTicketsList() {
        return List.of();
    }

    @Override
    public EnableTicketRespond enableTicket(Long id) throws ParseException {
        return null;
    }

    @Override
    public void UserDeleteTicket(Long id) {

    }

}
