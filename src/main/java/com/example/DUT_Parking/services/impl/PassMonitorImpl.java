package com.example.DUT_Parking.services.impl;

import com.example.DUT_Parking.DTO.PassRequest;
import com.example.DUT_Parking.DTO.TicketRequest;
import com.example.DUT_Parking.entity.PassMonitor;
import com.example.DUT_Parking.entity.UserTicketsInfo;
import com.example.DUT_Parking.entity.UsersProfile;
import com.example.DUT_Parking.enums.TicketStatus;
import com.example.DUT_Parking.repository.PassMonitorRepo;
import com.example.DUT_Parking.repository.UserTicketsRepo;
import com.example.DUT_Parking.respond.GetProfileRespond;
import com.example.DUT_Parking.respond.GetUserTicketsListRespond;
import com.example.DUT_Parking.respond.TicketRespond;
import com.example.DUT_Parking.services.AdminServices;
import com.example.DUT_Parking.services.PassMonitorService;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.List;

@Slf4j
@Service("passMonitorImpl")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE , makeFinal = true)
public class PassMonitorImpl implements PassMonitorService , AdminServices {
    PassMonitorRepo passMonitorRepo;
    UserTicketsRepo userTicketsRepo;

    public void HandlePassData (PassRequest request) throws ParseException {
        SignedJWT passToken = SignedJWT.parse(request.getPassToken());
        JWTClaimsSet claims = passToken.getJWTClaimsSet();
        Long id = claims.getLongClaim("id");
        String hovaten = claims.getStringClaim("hovaten");
        String email = claims.getStringClaim("email");
        String ticketName = claims.getStringClaim("ticketName");
        String decision = claims.getStringClaim("decision");

        PassMonitor passMonitor = PassMonitor.builder()
                .hovaten(hovaten)
                .email(email)
                .ticketName(ticketName)
                .decision(decision)
                .build();
        if (decision.equals("NOT PASS")){
            UserTicketsInfo userTicketsInfo = userTicketsRepo.findById(id);
            userTicketsInfo.setStatus(TicketStatus.EXPIRED.name());
            userTicketsRepo.save(userTicketsInfo);
        }
        if (passMonitorRepo.findByEmail(email) == null) {
            passMonitorRepo.save(passMonitor);
        }
    }

    @Override
    public void deleteUserProfile(Long id) {

    }

    @Override
    public List<UsersProfile> getAllUsersProfile() {
        return List.of();
    }

    @Override
    public GetProfileRespond SearchUserProfile(String hovaten) {
        return null;
    }

    @Override
    public TicketRespond createTicket(TicketRequest request) {
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
    public void AdminDeleteTicket(Long id) {

    }

    @Override
    public List<UserTicketsInfo> getAllUserTickets() {
        return List.of();
    }

    @Override
    public List<GetUserTicketsListRespond> findUserTicket(String email) {
        return List.of();
    }

    public List<PassMonitor> getAllPassData() {
        return passMonitorRepo.findAll();
    }

    @Transactional
    public void deleteAllPassData() {
        passMonitorRepo.deleteAll();
    }
}
