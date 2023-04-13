package com.example.countryNeighborsTour.service;

import com.example.countryNeighborsTour.dto.UserDTO;
import com.example.countryNeighborsTour.exeptions.InvalidInputException;
import com.example.countryNeighborsTour.model.User;
import com.example.countryNeighborsTour.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@AllArgsConstructor
@Service
public class UserServiceImpl implements UserService{

    private final UserRepository userRepository;

    @Override
    public String registration(String username, String password) {

        Pattern pattern = Pattern.compile("^[a-zA-Z0-9_-]{2,50}$");
        Matcher matcher = pattern.matcher(username);
        if (!matcher.matches() || username.equalsIgnoreCase("null")){
            throw new InvalidInputException("Incorrect username");
        }

        pattern = Pattern.compile("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{5,20}$");
        matcher = pattern.matcher(password);

        if (!matcher.matches()){
            throw new InvalidInputException("Incorrect password");
        }

        if (username.isEmpty()) {
            throw new InvalidInputException("User name cannot be null or empty");
        } else if (password.isEmpty()) {
            throw new InvalidInputException("User password cannot be null or empty");
        }
        User user = new User(username, password);
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

        if (!userRepository.findByUserName(username).isPresent()){
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            userRepository.save(user);
            return user.getUserName() + " has registered completely";
        }
        return "Username already exists!";
    }

    @Override
    public List<UserDTO> readAllUsers(String username) {

        if (userRepository.findByUserName(username).isPresent()) {
            if (userRepository.findByUserName(username).get().getIsOnline() == 1) {
                List<User> users = userRepository.findAll();
                List<UserDTO> response = new ArrayList<>();

                for (User user : users) {
                    response.add(new UserDTO(user.getUserName()));
                }
                return response;
            }
            throw new InvalidInputException(username + " is offline");
        }
        throw new InvalidInputException("User " + username + " does not exists");
    }

    @Override
    public String deleteById(Long id, String username) {

        Optional<User> user = findByUserName(username);
        if (!user.isPresent()){
            throw new InvalidInputException("User " + username + " does not exists");
        }
        else {
            if (user.get().getIsOnline() == 0){
                throw new InvalidInputException(username + " is offline");
            }
        }

        if (id == null || id <= 0) {
            throw new InvalidInputException("The input cannot be null, less than zero or equal to zero");
        }

        if (userRepository.findById(id).isPresent()){
            userRepository.deleteById(id);
            return "User deleted successfully";
        }
        return "User not found!";
    }

    @Override
    public String setUserStatus(String username) {

        Optional<User> user = userRepository.findByUserName(username);

        if (user.isPresent()) {
            if (user.get().getIsOnline() == 1) {
                user.get().setIsOnline(0);
                userRepository.save(user.get());
                return user.get().getUserName() + " has logged out successfully";
            } else {
                user.get().setIsOnline(1);
                userRepository.save(user.get());
                return user.get().getUserName() + " has logged in successfully";
            }
        }
        return "Invalid username!";
    }

    @Override
    public Optional<User> findByUserName(String userName) {

        return userRepository.findByUserName(userName);
    }
}