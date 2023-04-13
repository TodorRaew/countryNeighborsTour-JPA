package com.example.countryNeighborsTour.service;

import com.example.countryNeighborsTour.dto.TravelRequest;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.util.Map;

public interface CountryService {

    int calculateCountOfVisits(TravelRequest travelRequest);
    String saveCountry(String name, String abrev, String username);
    String removeCountry(String name, String username);
    Map<String, Double> getRates() throws JsonProcessingException;
    StringBuilder execute(String username, TravelRequest travelRequest) throws JsonProcessingException;
    StringBuilder convertCurrency(String username, TravelRequest travelRequest) throws JsonProcessingException;
}
