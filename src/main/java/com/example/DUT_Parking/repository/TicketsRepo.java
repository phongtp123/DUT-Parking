package com.example.DUT_Parking.repository;

import com.example.DUT_Parking.entity.Tickets;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TicketsRepo extends JpaRepository<Tickets, String> {
    Tickets findByName(String name);
}
