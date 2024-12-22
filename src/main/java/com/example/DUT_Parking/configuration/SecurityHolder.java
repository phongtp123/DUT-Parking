package com.example.DUT_Parking.configuration;

import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class SecurityHolder {
    public SecurityContext getContext() {
        return SecurityContextHolder.getContext();
    }
}
