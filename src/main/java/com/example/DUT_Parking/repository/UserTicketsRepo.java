package com.example.DUT_Parking.repository;

import com.example.DUT_Parking.entity.UserTicketsInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserTicketsRepo extends JpaRepository<UserTicketsInfo,String> {
    List<UserTicketsInfo> findAllByEmail(String email);

    void deleteById(Long id);

    UserTicketsInfo findById(Long id);

    UserTicketsInfo findByEmail(String email);
}
