package com.example.countryNeighborsTour.controller;

import com.example.countryNeighborsTour.dto.TravelRequest;
import com.example.countryNeighborsTour.service.CountryService;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@Validated
@AllArgsConstructor
@RestController
@RequestMapping("/api/v4")
public class CountryController {

    private final CountryService countryService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("country/{name}/{abrev}")
    public String saveCountry(@PathVariable String name,
                              @PathVariable String abrev,
                              @AuthenticationPrincipal UserDetails userDetails) {

        return countryService.saveCountry(name, abrev, userDetails.getUsername());
    }

    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping("country/{name}")
    public String removeCountry(@PathVariable String name,
                                @AuthenticationPrincipal UserDetails userDetails) {

        return countryService.removeCountry(name, userDetails.getUsername());
    }


    @ResponseStatus(HttpStatus.OK)
    @GetMapping("result")
    public StringBuilder execute(@RequestParam String startingCountry,
                                 @RequestParam BigDecimal budgetPerCountry,
                                 @RequestParam BigDecimal totalBudget,
                                 @AuthenticationPrincipal UserDetails userDetails) throws JsonProcessingException {
        TravelRequest travelRequest = new TravelRequest(startingCountry, budgetPerCountry, totalBudget);

        return countryService.execute(userDetails.getUsername(), travelRequest);
    }
}

