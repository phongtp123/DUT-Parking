package com.example.DUT_Parking.services.impl;

import com.example.DUT_Parking.DTO.*;
import com.example.DUT_Parking.entity.Tickets;
import com.example.DUT_Parking.entity.UserTicketsInfo;
import com.example.DUT_Parking.entity.UsersProfile;
import com.example.DUT_Parking.enums.TicketStatus;
import com.example.DUT_Parking.mapper.TicketMapper;
import com.example.DUT_Parking.repository.TicketsRepo;
import com.example.DUT_Parking.repository.UserTicketsRepo;
import com.example.DUT_Parking.repository.UsersProfileRepo;
import com.example.DUT_Parking.respond.*;
import com.example.DUT_Parking.services.AdminServices;
import com.example.DUT_Parking.services.UserServices;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import net.glxn.qrgen.core.image.ImageType;
import net.glxn.qrgen.javase.QRCode;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.io.*;
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
                    .qr_code(ticket.getQr_code())
                    .build();

            ticketsListRespond.add(respond);
        }

        return ticketsListRespond;
    }

    @Transactional
    public void UserDeleteTicket(Long id) {
        userTicketsRepo.deleteById(id);
    }

    @Transactional
    public void AdminDeleteTicket(Long id) {
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
        requestTicket.setQr_code(qr_code_maker(ticket_token));
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
                .claim("id" , id)
                .claim("ticketName" , ticketName)
                .issueTime(new Date())
                .expirationTime(ExpiriDate(id))
                .claim("Price" , TicketPrice(id))
                .claim("email" , email)
                .claim("hovaten" , hovaten)
                .build();
        Payload payload = new Payload(jwtClaimsSet.toJSONObject());
        JWSObject jwsObject = new JWSObject(header, payload);
        try {
            String base64EncodedKey = Base64.getEncoder().encodeToString(signer_key.getBytes());
            jwsObject.sign(new MACSigner(base64EncodedKey));
            return jwsObject.serialize();
        } catch (JOSEException e) {
            log.error("Cannot generate ticket" ,e);
            throw new RuntimeException(e);
        }
    }


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


    private byte[] qr_code_maker(String ticket_token) {
        var data = ticket_token;
        ByteArrayOutputStream stream = QRCode.from(data).withSize(250 , 250).to(ImageType.PNG)
                .stream();

        try {
            // Tạo thư mục nếu chưa tồn tại
            File folder = new File("E:/Workspace/qr_code_image");
            if (!folder.exists()) {
                folder.mkdir();
            }

            // Tạo file và ghi dữ liệu từ stream
            File file = new File(folder, String.valueOf(ImageType.PNG)); // Lưu với tên là token
            try (FileOutputStream fos = new FileOutputStream(file)) {
                fos.write(stream.toByteArray());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }

        return stream.toByteArray();
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
