package com.example.DUT_Parking.configuration;

import com.example.DUT_Parking.entity.RegisteredUsers;
import com.example.DUT_Parking.entity.UsersProfile;
import com.example.DUT_Parking.enums.Roles;
import com.example.DUT_Parking.repository.RegisteredUserRepo;
import com.example.DUT_Parking.repository.UsersProfileRepo;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashSet;

@Configuration
public class InitConfig {

    @Bean
    ApplicationRunner applicationRunner(RegisteredUserRepo registeredUserRepo, UsersProfileRepo usersProfileRepo) {
        return args -> {
            if (usersProfileRepo.findByEmail("admin@gmail.com") == null) {
                var role = new HashSet<String>();
                role.add(Roles.ADMIN.name());
                UsersProfile admin = UsersProfile.builder()
                        .email("admin@gmail.com")
                        .password("admin")
                        .roles(role)
                        .build();
                usersProfileRepo.save(admin);
            }
            if (registeredUserRepo.findByEmail("admin@gmail.com") == null ) {
                RegisteredUsers admin_reg = RegisteredUsers.builder()
                        .email("admin@gmail.com")
                        .password("admin")
                        .build();
                registeredUserRepo.save(admin_reg);
            }
        };
    }
}
