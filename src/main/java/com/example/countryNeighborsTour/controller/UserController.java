package com.example.countryNeighborsTour.controller;

import com.example.countryNeighborsTour.dto.UserDTO;
import com.example.countryNeighborsTour.dto.UserRequest;
import com.example.countryNeighborsTour.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Validated
@AllArgsConstructor
@RestController
@RequestMapping("/api/v3")
public class UserController {

    private final UserService userService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("users")
    public String registration(@RequestBody UserRequest request) {
        return userService.registration(request.getUsername(), request.getPassword());
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("allUsers")
    public List<UserDTO> getAllUsers(@AuthenticationPrincipal UserDetails userDetails) {
        return userService.readAllUsers(userDetails.getUsername());
    }

    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping("userById/{id}")
    public String deleteById(@PathVariable("id") Long id,
                             @AuthenticationPrincipal UserDetails userDetails) {
        return userService.deleteById(id, userDetails.getUsername());
    }

    @ResponseStatus(HttpStatus.OK)
    @PatchMapping("status")
    public String setUserStatus(@AuthenticationPrincipal UserDetails userDetails) {
        return userService.setUserStatus(userDetails.getUsername());
    }
}