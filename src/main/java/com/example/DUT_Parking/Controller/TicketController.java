package com.example.DUT_Parking.Controller;

import com.example.DUT_Parking.DTO.BuyTicketRequest;
import com.example.DUT_Parking.DTO.TicketCreate;
import com.example.DUT_Parking.entity.UserTicketsInfo;
import com.example.DUT_Parking.respond.*;
import com.example.DUT_Parking.services.AdminServices;
import com.example.DUT_Parking.services.UserServices;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.util.List;

@RestController
@RequestMapping("/services")
@FieldDefaults(level = AccessLevel.PRIVATE , makeFinal = true)
public class TicketController {
    private  final UserServices userServices;
    private final AdminServices adminServices;

    public TicketController(@Qualifier("ticketImpl") UserServices userServices, @Qualifier("ticketImpl") AdminServices adminServices) {
        this.userServices = userServices;
        this.adminServices = adminServices;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/ticket/create-ticket")
    APIRespond<TicketRespond> createTicket (@RequestBody TicketCreate request){
        return APIRespond.<TicketRespond>builder()
                .result(adminServices.createTicket(request))
                .build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/ticket/tickets-list")
    List<GetTicketTypeList> getAllTickets(){
        return adminServices.getAllTickets();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/ticket/tickets-list/{ticketId}")
    String deleteTicket(@PathVariable String ticketId){
        adminServices.deleteTicket(ticketId);
        return String.format("Ticket %s has been delete successfully", ticketId);
    }

    @PreAuthorize("hasRole('USER')")
    @PostMapping("/ticket/buy-ticket")
    APIRespond<BuyTicketRespond> buyTicket (@RequestBody BuyTicketRequest request){
        return APIRespond.<BuyTicketRespond>builder()
                .result(userServices.buyTicket(request))
                .build();
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/ticket/my-tickets-list")
    List<GetUserTicketsListRespond> getUserTicketsList(){
        return userServices.getUserTicketsList();
    }

    @PreAuthorize("hasRole('USER')")
    @DeleteMapping("/ticket/my-tickets-list/{id}")
    APIRespond<Void> UserDeleteTicket(@PathVariable Long id){
        userServices.UserDeleteTicket(id);
        return APIRespond.<Void>builder().build();
    }

    @PreAuthorize("hasRole('USER')")
    @PostMapping("/ticket/my-tickets-list/enable-ticket/{id}")
    APIRespond<EnableTicketRespond> enableTicket (@PathVariable Long id) throws ParseException {
        return APIRespond.<EnableTicketRespond>builder()
                .result(userServices.enableTicket(id))
                .build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/ticket/all-user-tickets")
    List<GetAllUserTicketsListRespond> getAllUserTickets(){
        return adminServices.getAllUserTickets();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/ticket/all-user-tickets/{MSSV}")
    List<GetAllUserTicketsListRespond> findUserTicket(@PathVariable String MSSV){
        return adminServices.findUserTicket(MSSV);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/ticket/all-user-tickets/{id}")
    APIRespond<Void> AdminDeleteTicket(@PathVariable Long id){
        adminServices.AdminDeleteTicket(id);
        return APIRespond.<Void>builder().build();
    }

}
