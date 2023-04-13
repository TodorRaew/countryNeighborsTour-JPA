package com.example.countryNeighborsTour.service;

import com.example.countryNeighborsTour.dto.UserDTO;
import com.example.countryNeighborsTour.model.User;

import java.util.List;
import java.util.Optional;

public interface UserService {
    String registration(String username, String password);

    List<UserDTO> readAllUsers(String username);

    String deleteById(Long id, String username);

    String setUserStatus(String username);

    Optional<User> findByUserName(String userName);
}