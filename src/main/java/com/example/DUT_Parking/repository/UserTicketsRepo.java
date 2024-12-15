package com.example.DUT_Parking.repository;

import com.example.DUT_Parking.entity.UserTicketsInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserTicketsRepo extends JpaRepository<UserTicketsInfo,String> {
    @Query("SELECT ut FROM UserTicketsInfo ut WHERE ut.usersProfile.MSSV = :MSSV")
    List<UserTicketsInfo> findAllByMSSV(@Param("MSSV") String MSSV);

    @Query("SELECT ut FROM UserTicketsInfo ut WHERE ut.usersProfile.email = :email")
    List<UserTicketsInfo> findAllByEmail(@Param("email") String email);

    void deleteById(Long id);

    @Query("SELECT ut FROM UserTicketsInfo ut WHERE ut.usersProfile.MSSV = :MSSV")
    void deleteByMSSV(@Param("MSSV") String MSSV);

    UserTicketsInfo findById(Long id);

}
