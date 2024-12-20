package com.example.DUT_Parking.services;

import com.example.DUT_Parking.DTO.PassRequest;

import java.text.ParseException;

public interface PassMonitorService {
    void HandlePassData (PassRequest request) throws ParseException;
}
