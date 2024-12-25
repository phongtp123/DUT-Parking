package com.example.DUT_Parking.services.impl;

import com.example.DUT_Parking.DTO.PassRequest;
import com.example.DUT_Parking.DTO.TicketCreate;
import com.example.DUT_Parking.configuration.JWTParser;
import com.example.DUT_Parking.entity.PassMonitor;
import com.example.DUT_Parking.entity.Tickets;
import com.example.DUT_Parking.entity.UserTicketsInfo;
import com.example.DUT_Parking.entity.UsersProfile;
import com.example.DUT_Parking.enums.TicketStatus;
import com.example.DUT_Parking.repository.PassMonitorRepo;
import com.example.DUT_Parking.repository.UserTicketsRepo;
import com.example.DUT_Parking.repository.UsersProfileRepo;
import com.example.DUT_Parking.respond.*;
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
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service("passMonitorImpl")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE , makeFinal = true)
public class PassMonitorImpl implements PassMonitorService , AdminServices {
    PassMonitorRepo passMonitorRepo;
    UserTicketsRepo userTicketsRepo;
    UsersProfileRepo usersProfileRepo;
    JWTParser jwtParser;

    public void HandlePassData (PassRequest request) throws ParseException {
        SignedJWT passToken = jwtParser.parse(request.getPassToken());
        JWTClaimsSet claims = passToken.getJWTClaimsSet();
        Long id = claims.getLongClaim("id");
//        String hovaten = claims.getStringClaim("hovaten");
//        String email = claims.getStringClaim("email");
//        String ticketName = claims.getStringClaim("ticketName");
        String decision = claims.getStringClaim("decision");
        var ticket = userTicketsRepo.findById(id);
        //var ticketType = ticket.getTickets();
        var profile = ticket.getUsersProfile();
        var passTime = LocalDate.now();
        PassMonitor passMonitor = PassMonitor.builder()
                .usersProfile(profile)
                .userTicketsInfo(ticket)
                .decision(decision)
                .passTime(passTime)
                .build();
        if (decision.equals("NOT PASS")){
            ticket.setStatus(TicketStatus.EXPIRED.name());
        }
        passMonitorRepo.save(passMonitor);
        profile.getPassMonitors().add(passMonitor);
        ticket.getPassMonitors().add(passMonitor);
        userTicketsRepo.save(ticket);
        usersProfileRepo.save(profile);
    }

    @Override
    public List<GetProfileRespond> getAllUsersProfile() {
        return List.of();
    }

    @Override
    public List<GetProfileRespond> SearchUserProfile(String hovaten) {
        return null;
    }

    @Override
    public TicketRespond createTicket(TicketCreate request) {
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
    public void AdminDeleteTicket(Long id) {

    }

    @Override
    public List<GetAllUserTicketsListRespond> getAllUserTickets() {
        return List.of();
    }

    @Override
    public List<GetAllUserTicketsListRespond> findUserTicket(String email) {
        return List.of();
    }

    public List<GetAllPassDataRespond> getAllPassData() {
        List<PassMonitor> passMonitors = passMonitorRepo.findAll();
        return passMonitors.stream().map(passMonitor -> {
            var ticket = passMonitor.getUserTicketsInfo();
            var profile = passMonitor.getUsersProfile();
            var ticketType = ticket.getTickets();
            GetAllPassDataRespond getAllPassDataRespond = new GetAllPassDataRespond();
            getAllPassDataRespond.setId(passMonitor.getId());
            getAllPassDataRespond.setHovaten(profile.getHovaten());
            getAllPassDataRespond.setEmail(profile.getEmail());
            getAllPassDataRespond.setTicketName(ticketType.getTicketName());
            getAllPassDataRespond.setDecision(passMonitor.getDecision());
            getAllPassDataRespond.setPassTime(passMonitor.getPassTime());
            return getAllPassDataRespond;
        }).collect(Collectors.toList());
    }

    @Transactional
    public void deleteAllPassData() {
        passMonitorRepo.deleteAll();
    }
}
