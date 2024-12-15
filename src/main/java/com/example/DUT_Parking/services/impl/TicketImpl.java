package com.example.DUT_Parking.services.impl;

import com.example.DUT_Parking.DTO.*;
import com.example.DUT_Parking.entity.*;
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
import java.util.stream.Collectors;

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

    public TicketRespond createTicket(TicketCreate request) {
        Tickets tickets = Tickets.builder()
                .ticketName(request.getTicketName())
                .menhgia(request.getMenhgia())
                .build();
        ticketsRepo.save(tickets);
        return TicketRespond.builder()
                .message("Ticket create success!")
                .build();
    }

    public BuyTicketRespond buyTicket(BuyTicketRequest request) {
        var info = SecurityContextHolder.getContext();
        var rawinfo = info.getAuthentication().getName();
        UsersProfile userinfo = usersProfileRepo.findByEmail(rawinfo);
        Tickets ticketType = ticketsRepo.findByTicketName(request.getName());
        var menhgia = ticketType.getMenhgia();
        var ticketName = ticketType.getTicketName();
        var status = TicketStatus.DISABLE.name();
        var last_sodu = userinfo.getSodu();
        if (last_sodu < menhgia){
            throw new AppException(ErrorCode.INSUFFICIENT_FUNDS);
        }
        var latest_sodu = last_sodu - menhgia;
        userinfo.setSodu(latest_sodu);
        UserTicketsInfo ticket = UserTicketsInfo.builder()
                .usersProfile(userinfo)
                .tickets(ticketType)
                .status(status)
                .build();
        ticketType.getUserTicketsInfos().add(ticket);
        userinfo.getUserTicketsInfos().add(ticket);
        ticketsRepo.save(ticketType);
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
            var ticketType = ticket.getTickets();
            GetUserTicketsListRespond respond = GetUserTicketsListRespond.builder()
                    .ticketId(ticket.getId())
                    .ticketName(ticketType.getTicketName())
                    .issueDate(ticket.getIssueDate())
                    .expiryDate(ticket.getExpiryDate())
                    .menhgia(ticketType.getMenhgia())
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
    public void AdminDeleteTicket(String MSSV) {
        userTicketsRepo.deleteByMSSV(MSSV);
    }

    public  List<GetAllUserTicketsListRespond> getAllUserTickets() {

        List<UserTicketsInfo> userTicketsInfos = userTicketsRepo.findAll();

        return userTicketsInfos.stream().map(userTicketsInfo -> {
            var ticketType = userTicketsInfo.getTickets();
            var profile = userTicketsInfo.getUsersProfile();
            GetAllUserTicketsListRespond getAllUserTicketsListRespond = new GetAllUserTicketsListRespond();
            getAllUserTicketsListRespond.setTicketId(userTicketsInfo.getId());
            getAllUserTicketsListRespond.setEmail(profile.getEmail());
            getAllUserTicketsListRespond.setMSSV(profile.getMSSV());
            getAllUserTicketsListRespond.setTicketName(ticketType.getTicketName());
            getAllUserTicketsListRespond.setIssueDate(userTicketsInfo.getIssueDate());
            getAllUserTicketsListRespond.setExpiryDate(userTicketsInfo.getExpiryDate());
            getAllUserTicketsListRespond.setMenhgia(ticketType.getMenhgia());
            getAllUserTicketsListRespond.setStatus(userTicketsInfo.getStatus());
            return getAllUserTicketsListRespond;
        }).collect(Collectors.toList());
    }

    public List<GetAllUserTicketsListRespond> findUserTicket (String MSSV) {
        List<UserTicketsInfo> userTicketsList = userTicketsRepo.findAllByMSSV(MSSV);

        List<GetAllUserTicketsListRespond> ticketsListRespond = new ArrayList<>();

        for (UserTicketsInfo ticket : userTicketsList) {
            var ticketType = ticket.getTickets();
            var profile = ticket.getUsersProfile();
            GetAllUserTicketsListRespond respond = GetAllUserTicketsListRespond.builder()
                    .ticketId(ticket.getId())
                    .email(profile.getEmail())
                    .MSSV(profile.getMSSV())
                    .ticketName(ticketType.getTicketName())
                    .issueDate(ticket.getIssueDate())
                    .expiryDate(ticket.getExpiryDate())
                    .menhgia(ticketType.getMenhgia())
                    .status(ticket.getStatus())
                    .build();

            ticketsListRespond.add(respond);
        }
        return ticketsListRespond;
    }

    @Override
    public List<GetAllPassDataRespond> getAllPassData() {
        return List.of();
    }

    @Override
    public void deleteAllPassData() {

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

    public List<GetTicketTypeList> getAllTickets() {
        List<Tickets> tickets = ticketsRepo.findAll();

        return tickets.stream().map(ticket -> {
            GetTicketTypeList getTicketTypeList = new GetTicketTypeList();
            getTicketTypeList.setTicketName(ticket.getTicketName());
            getTicketTypeList.setMenhgia(ticket.getMenhgia());
            return getTicketTypeList;
        }).collect(Collectors.toList());
    }

    public void deleteTicket(String ticketId) {
        ticketsRepo.deleteById(ticketId);
    }

    private String generateTicket(Long id) {
        JWSHeader header = new JWSHeader(JWSAlgorithm.HS256);
        UserTicketsInfo ticket = userTicketsRepo.findById(id);
        var ticketType = ticket.getTickets();
        var ticketName = ticketType.getTicketName();
        var profile = ticket.getUsersProfile();
        var email = profile.getEmail();
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
        var ticketType = ticket.getTickets();
        var ticket_info = ticketType.getTicketName();
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
        UserTicketsInfo ticket = userTicketsRepo.findById(id);
        var ticketType = ticket.getTickets();
        return ticketType.getMenhgia();
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
    public void deleteUserProfile(String MSSV) {

    }

    @Override
    public List<GetProfileRespond> getAllUsersProfile() {
        return List.of();
    }

    @Override
    public List<GetProfileRespond> SearchUserProfile(String hovaten) {
        return null;
    }
}
