package com.example.DUT_Parking.services.impl;

import com.example.DUT_Parking.DTO.*;
import com.example.DUT_Parking.entity.Tickets;
import com.example.DUT_Parking.entity.UserTicketsInfo;
import com.example.DUT_Parking.entity.UsersProfile;
import com.example.DUT_Parking.enums.TicketStatus;
import com.example.DUT_Parking.exception_handling.AppException;
import com.example.DUT_Parking.exception_handling.ErrorCode;
import com.example.DUT_Parking.mapper.TicketMapper;
import com.example.DUT_Parking.repository.TicketsRepo;
import com.example.DUT_Parking.repository.UserTicketsRepo;
import com.example.DUT_Parking.repository.UsersProfileRepo;
import com.example.DUT_Parking.respond.*;
import com.example.DUT_Parking.services.AdminServices;
import com.example.DUT_Parking.services.UserServices;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;

import static com.example.DUT_Parking.services.impl.AuthenticationImpl.signer_key;

@Slf4j
@Service("ticketImpl")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE , makeFinal = true)
public class TicketImpl implements UserServices, AdminServices {
    UsersProfileRepo usersProfileRepo;
    TicketsRepo ticketsRepo;
    TicketMapper ticketMapper;
    UserTicketsRepo userTicketsRepo;

    public TicketRespond createTicket(TicketRequest request) {
        int ticketPrice = 0;
        var ticket_info = request.getName();
        if (ticket_info.equals("VE NGAY")) {
            ticketPrice = 1500;
        } else if (ticket_info.equals("VE TUAN")) {
            ticketPrice = 9000;
        } else if (ticket_info.equals("VE THANG")) {
            ticketPrice = 39000;
        }
        Tickets tickets = Tickets.builder()
                .name(ticket_info)
                .menhgia(ticketPrice)
                .build();
        ticketsRepo.save(tickets);
        return TicketRespond.builder()
                .name(request.getName())
                .menhgia(ticketPrice)
                .build();
    }

    public BuyTicketRespond buyTicket(BuyTicketRequest request) {
        var info = SecurityContextHolder.getContext();
        var rawinfo = info.getAuthentication().getName();
        UsersProfile userinfo = usersProfileRepo.findByEmail(rawinfo);
        Tickets ticketType = ticketsRepo.findByName(request.getName());
        var menhgia = ticketType.getMenhgia();
        var ticketName = ticketType.getName();
        var status = TicketStatus.DISABLE.name();
        var last_sodu = userinfo.getSodu();
        var latest_sodu = last_sodu - menhgia;
        userinfo.setSodu(latest_sodu);


        UserTicketsInfo ticket = UserTicketsInfo.builder()
                .email(userinfo.getEmail())
                .ticketName(ticketName)
                .menhgia(menhgia)
                .status(status)
                .build();
        userTicketsRepo.save(ticket);
        usersProfileRepo.save(userinfo);
        return BuyTicketRespond.builder()
                .status(true)
                .message(String.format("Mua thanh cong %s , vui long kiem tra Ticket List cua ban" , ticketName))
                .build();
    }

    public List<GetUserTicketsListRespond> getUserTicketsList() {
        var getInfo = SecurityContextHolder.getContext().getAuthentication().getName();
        List<UserTicketsInfo> ticketsList = userTicketsRepo.findAllByEmail(getInfo);

        List<GetUserTicketsListRespond> ticketsListRespond = new ArrayList<>();

        for (UserTicketsInfo ticket : ticketsList) {
            GetUserTicketsListRespond respond = GetUserTicketsListRespond.builder()
                    .ticketId(ticket.getId())
                    .ticketName(ticket.getTicketName())
                    .issueDate(ticket.getIssueDate())
                    .expiryDate(ticket.getExpiryDate())
                    .menhgia(ticket.getMenhgia())
                    .status(ticket.getStatus())
                    .build();

            ticketsListRespond.add(respond);
        }

        return ticketsListRespond;
    }

    public void DeleteTicket(Long id) {
        userTicketsRepo.deleteById(id);
    }

    public List<UserTicketsInfo> getAllUserTickets() {
        return userTicketsRepo.findAll();
    }

    public EnableTicketRespond enableTicket(Long id) throws ParseException {

        var requestTicket = userTicketsRepo.findById(id);
        requestTicket.setStatus(TicketStatus.ENABLE.name());

        var ticket_token = generateTicket(id);
        var ticket_info = ticketInfo(ticket_token);
        var issueDate = ticket_info.getJWTClaimsSet().getIssueTime();
        var expiryDate = ticket_info.getJWTClaimsSet().getExpirationTime();
        requestTicket.setIssueDate(issueDate);
        requestTicket.setExpiryDate(expiryDate);

        userTicketsRepo.save(requestTicket);
        return EnableTicketRespond.builder()
                .status(true)
                .message("Enable ticket success")
                .ticketToken(ticket_token)
                .build();
    }

    private SignedJWT ticketInfo (String token) throws ParseException {
        return SignedJWT.parse(token);
    }

    public List<TicketRespond> getAllTickets() {
        return ticketsRepo.findAll().stream().map(ticketMapper::toTicketRespond).toList();
    }

    public void deleteTicket(String ticket_name) {
        ticketsRepo.deleteById(ticket_name);
    }

    private String generateTicket(Long id) {
        JWSHeader header = new JWSHeader(JWSAlgorithm.HS256);
        UserTicketsInfo ticket = userTicketsRepo.findById(id);
        var ticketName = ticket.getTicketName();
        var email = ticket.getEmail();
        UsersProfile userinfo = usersProfileRepo.findByEmail(email);
        var hovaten = userinfo.getHovaten();
        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
                .jwtID(UUID.randomUUID().toString())
                .issuer("example.com")
                .subject(ticketName)
                .issueTime(new Date())
                .expirationTime(ExpiriDate(id))
                .claim("Price" , TicketPrice(id))
                .claim("UserEmail" , email)
                .claim("Hovaten" , hovaten)
                .build();
        Payload payload = new Payload(jwtClaimsSet.toJSONObject());
        JWSObject jwsObject = new JWSObject(header, payload);
        try {
            jwsObject.sign(new MACSigner(signer_key.getBytes()));
            return jwsObject.serialize();
        } catch (JOSEException e) {
            log.error("Cannot generate ticket" ,e);
            throw new RuntimeException(e);
        }
    }

//    private SignedJWT verify_ticket(String ticket_token) throws ParseException, JOSEException {
//        JWSVerifier verifier = new MACVerifier(signer_key.getBytes());
//        SignedJWT signedJWT = SignedJWT.parse(ticket_token);
//        Date expirationDate = signedJWT.getJWTClaimsSet().getExpirationTime();
//        var validation = signedJWT.verify(verifier);
//        if (!(validation && expirationDate.after(new Date()))){
//            throw new AppException(ErrorCode.UNAUTHENTICATED);
//        }
//        if (logoutUserRepo.existsById(signedJWT.getJWTClaimsSet().getJWTID())) {
//            throw new AppException(ErrorCode.UNAUTHENTICATED);
//        }
//        return signedJWT;
//    }

    private Date ExpiriDate(Long id) {
        Date expDate = new Date();
        UserTicketsInfo ticket = userTicketsRepo.findById(id);
        var ticket_info = ticket.getTicketName();
        if (ticket_info.equals("VE NGAY")) {
            expDate = new Date(Instant.now().plus(1, ChronoUnit.DAYS).toEpochMilli());
        } else if (ticket_info.equals("VE TUAN")) {
            expDate = new Date(Instant.now().plus(7, ChronoUnit.DAYS).toEpochMilli());
        } else if (ticket_info.equals("VE THANG")) {
            expDate = new Date(Instant.now().plus(30, ChronoUnit.DAYS).toEpochMilli());
        }
        return expDate;
    }

    private int TicketPrice(Long id) {
        int Price = 0;
        UserTicketsInfo ticket = userTicketsRepo.findById(id);
        var ticket_info = ticket.getTicketName();
        if (ticket_info.equals("VE NGAY")) {
            Price = 1500;
        } else if (ticket_info.equals("VE TUAN")) {
            Price = 9000;
        } else if (ticket_info.equals("VE THANG")) {
            Price = 39000;
        }
        return Price;
    }

    @Override
    public UpdateRespond UpdateProfile(UpdateRequest user_profile) {
        return null;
    }

    @Override
    public GetProfileRespond GetUserProfile() {
        return null;
    }

    @Override
    public RechargeRespond recharge(RechargeRequest request) {
        return null;
    }

    @Override
    public void deleteUserProfile(String hovaten) {

    }

    @Override
    public List<UsersProfile> getAllUsersProfile() {
        return List.of();
    }

    @Override
    public GetProfileRespond SearchUserProfile(String hovaten) {
        return null;
    }
}
