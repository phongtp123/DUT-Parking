package com.example.DUT_Parking.services.impl;

import com.example.DUT_Parking.DTO.*;
import com.example.DUT_Parking.entity.PassMonitor;
import com.example.DUT_Parking.entity.UserTicketsInfo;
import com.example.DUT_Parking.entity.UsersProfile;
import com.example.DUT_Parking.exception_handling.AppException;
import com.example.DUT_Parking.exception_handling.ErrorCode;
import com.example.DUT_Parking.repository.UsersProfileRepo;
import com.example.DUT_Parking.respond.*;
import com.example.DUT_Parking.services.AdminServices;
import com.example.DUT_Parking.services.UserServices;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.List;
import java.util.stream.Collectors;

@Service("updateUserProfileImpl")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE , makeFinal = true)
public class UpdateUserProfileImpl implements UserServices , AdminServices {
    UsersProfileRepo usersProfileRepo;

    public UpdateRespond UpdateProfile (UpdateRequest user_profile) {
        var info = SecurityContextHolder.getContext();
        String name = info.getAuthentication().getName();
        UsersProfile userProfile = usersProfileRepo.findByEmail(name);
        userProfile.setMSSV(user_profile.getMSSV());
        if (userProfile.getMSSV() == null) {
            userProfile.setMSSV("GUEST");
        }
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
                .MSSV(usersProfile.getMSSV())
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

    @Override
    public void UserDeleteTicket(Long id) {

    }

    public List<GetProfileRespond> SearchUserProfile (String MSSV) {
        List<UsersProfile> searched_profile = usersProfileRepo.findByMSSV(MSSV);
        if (searched_profile == null) {
            throw new AppException(ErrorCode.USER_NOT_EXISTED);
        }
        return searched_profile.stream().map(profile -> {
                    GetProfileRespond getProfileRespond = new GetProfileRespond();
                    getProfileRespond.setMSSV(profile.getMSSV());
                    getProfileRespond.setEmail(profile.getEmail());
                    getProfileRespond.setHovaten(profile.getHovaten());
                    getProfileRespond.setSdt(profile.getSdt());
                    getProfileRespond.setDiachi(profile.getDiachi());
                    getProfileRespond.setQuequan(profile.getQuequan());
                    getProfileRespond.setSodu(profile.getSodu());
                    getProfileRespond.setDob(profile.getDob());
                    getProfileRespond.setGioitinh(profile.getGioitinh());
                    return getProfileRespond;
        }).collect(Collectors.toList());
    }

    @Override
    public TicketRespond createTicket(TicketCreate ticketCreate) {
        return null;
    }

    @Override
    public List<GetTicketTypeList> getAllTickets() {
        return List.of();
    }

    @Override
    public void deleteTicket(String ticket_name) {

    }

    @Override
    public void AdminDeleteTicket(String MSSV) {

    }

    @Override
    public List<GetAllUserTicketsListRespond> getAllUserTickets() {
        return List.of();
    }

    @Override
    public List<GetAllUserTicketsListRespond> findUserTicket(String email) {
        return List.of();
    }

    @Override
    public List<GetAllPassDataRespond> getAllPassData() {
        return List.of();
    }

    @Override
    public void deleteAllPassData() {

    }

    @Transactional
    public void deleteUserProfile (String MSSV) {
        usersProfileRepo.deleteById(MSSV);
    }

    public List<GetProfileRespond> getAllUsersProfile() {
        List<UsersProfile> getAllProfiles = usersProfileRepo.findAll();
        return getAllProfiles.stream().map(profile -> {
            GetProfileRespond getProfileRespond = new GetProfileRespond();
            getProfileRespond.setMSSV(profile.getMSSV());
            getProfileRespond.setEmail(profile.getEmail());
            getProfileRespond.setHovaten(profile.getHovaten());
            getProfileRespond.setSdt(profile.getSdt());
            getProfileRespond.setDiachi(profile.getDiachi());
            getProfileRespond.setQuequan(profile.getQuequan());
            getProfileRespond.setSodu(profile.getSodu());
            getProfileRespond.setDob(profile.getDob());
            getProfileRespond.setGioitinh(profile.getGioitinh());
            return getProfileRespond;
        }).collect(Collectors.toList());
    }

}
