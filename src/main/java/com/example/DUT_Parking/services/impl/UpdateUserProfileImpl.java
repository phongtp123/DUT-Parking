package com.example.DUT_Parking.services.impl;

import com.example.DUT_Parking.DTO.*;
import com.example.DUT_Parking.entity.UserTicketsInfo;
import com.example.DUT_Parking.entity.UsersProfile;
import com.example.DUT_Parking.exception_handling.AppException;
import com.example.DUT_Parking.exception_handling.ErrorCode;
import com.example.DUT_Parking.repository.UsersProfileRepo;
import com.example.DUT_Parking.respond.*;
import com.example.DUT_Parking.services.AdminServices;
import com.example.DUT_Parking.services.UserServices;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.List;

@Service("updateUserProfileImpl")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE , makeFinal = true)
public class UpdateUserProfileImpl implements UserServices , AdminServices {
    UsersProfileRepo usersProfileRepo;

    public UpdateRespond UpdateProfile (UpdateRequest user_profile) {
        var info = SecurityContextHolder.getContext();
        String name = info.getAuthentication().getName();
        UsersProfile userProfile = usersProfileRepo.findByEmail(name);
        userProfile.setHovaten(user_profile.getHovaten());
        userProfile.setSdt(user_profile.getSdt());
        userProfile.setDiachi(user_profile.getDiachi());
        userProfile.setQuequan(user_profile.getQuequan());
        userProfile.setGioitinh(user_profile.getGioitinh());
        userProfile.setDob(user_profile.getDob());
        usersProfileRepo.save(userProfile);
        return UpdateRespond.builder()
                .update_status(true)
                .message("Update user profile successfully")
                .build();
    }

    public GetProfileRespond GetUserProfile () {
        var info = SecurityContextHolder.getContext();
        String name = info.getAuthentication().getName();
        UsersProfile usersProfile = usersProfileRepo.findByEmail(name);
        if (usersProfile == null) {
            throw new AppException(ErrorCode.USER_NOT_EXISTED);
        }
        return GetProfileRespond.builder()
                .email(usersProfile.getEmail())
                .hovaten(usersProfile.getHovaten())
                .sdt(usersProfile.getSdt())
                .diachi(usersProfile.getDiachi())
                .quequan(usersProfile.getQuequan())
                .dob(usersProfile.getDob())
                .gioitinh(usersProfile.getGioitinh())
                .sodu(usersProfile.getSodu())
                .build();
    }

    @Override
    public RechargeRespond recharge(RechargeRequest request) {
        return null;
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

    public GetProfileRespond SearchUserProfile (String hovaten) {
        UsersProfile searched_profile = usersProfileRepo.findByHovaten(hovaten);
        if (searched_profile == null) {
            throw new AppException(ErrorCode.USER_NOT_EXISTED);
        }
        return GetProfileRespond.builder()
                .email(searched_profile.getEmail())
                .hovaten(searched_profile.getHovaten())
                .sdt(searched_profile.getSdt())
                .diachi(searched_profile.getDiachi())
                .quequan(searched_profile.getQuequan())
                .dob(searched_profile.getDob())
                .gioitinh(searched_profile.getGioitinh())
                .build();
    }

    @Override
    public TicketRespond createTicket(TicketRequest ticketRequest) {
        return null;
    }

    @Override
    public List<TicketRespond> getAllTickets() {
        return List.of();
    }

    @Override
    public void deleteTicket(String ticket_name) {

    }

    @Override
    public void DeleteTicket(Long id) {

    }

    @Override
    public List<UserTicketsInfo> getAllUserTickets() {
        return List.of();
    }

    public void deleteUserProfile (String hovaten) {
        usersProfileRepo.deleteByHovaten(hovaten);
    }

    public List<UsersProfile> getAllUsersProfile() {
        return usersProfileRepo.findAll();
    }

}
