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

    @PostMapping("/ticket/create-ticket")
    APIRespond<TicketRespond> createTicket (@RequestBody TicketCreate request){
        return APIRespond.<TicketRespond>builder()
                .result(adminServices.createTicket(request))
                .build();
    }

    @GetMapping("/ticket/tickets-list")
    APIRespond<List<GetTicketTypeList>> getAllTickets(){
        return APIRespond.<List<GetTicketTypeList>>builder()
                .result(adminServices.getAllTickets())
                .build();
    }

    @DeleteMapping("/ticket/{ticket_name}")
    APIRespond<Void> deleteTicket(@PathVariable String ticket_name){
        adminServices.deleteTicket(ticket_name);
        return APIRespond.<Void>builder().build();
    }

    @PostMapping("/ticket/buy-ticket")
    APIRespond<BuyTicketRespond> buyTicket (@RequestBody BuyTicketRequest request){
        return APIRespond.<BuyTicketRespond>builder()
                .result(userServices.buyTicket(request))
                .build();
    }

    @GetMapping("/ticket/my-tickets-list")
    APIRespond<List<GetUserTicketsListRespond>> getUserTicketsList(){
        return APIRespond.<List<GetUserTicketsListRespond>>builder()
                .result(userServices.getUserTicketsList())
                .build();
    }

    @DeleteMapping("/ticket/my-tickets-list/{id}")
    APIRespond<Void> UserDeleteTicket(@PathVariable Long id){
        userServices.UserDeleteTicket(id);
        return APIRespond.<Void>builder().build();
    }

    @PostMapping("/ticket/my-tickets-list/enable-ticket/{id}")
    APIRespond<EnableTicketRespond> enableTicket (@PathVariable Long id) throws ParseException {
        return APIRespond.<EnableTicketRespond>builder()
                .result(userServices.enableTicket(id))
                .build();
    }

    @GetMapping("/ticket/all-user-tickets")
    APIRespond<List<GetAllUserTicketsListRespond>> getAllUserTickets(){
        return APIRespond.<List<GetAllUserTicketsListRespond>>builder()
                .result(adminServices.getAllUserTickets())
                .build();
    }

    @GetMapping("/ticket/all-user-tickets/{MSSV}")
    APIRespond<List<GetAllUserTicketsListRespond>> findUserTicket(@PathVariable String MSSV){
        return APIRespond.<List<GetAllUserTicketsListRespond>>builder()
                .result(adminServices.findUserTicket(MSSV))
                .build();
    }

    @DeleteMapping("/ticket/all-user-tickets/{MSSV}")
    APIRespond<Void> AdminDeleteTicket(@PathVariable String MSSV){
        adminServices.AdminDeleteTicket(MSSV);
        return APIRespond.<Void>builder().build();
    }

}
