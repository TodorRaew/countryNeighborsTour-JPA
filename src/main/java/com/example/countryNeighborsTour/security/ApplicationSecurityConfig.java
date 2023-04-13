package com.example.countryNeighborsTour.security;

import com.example.countryNeighborsTour.model.User;
import com.example.countryNeighborsTour.repository.UserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

import java.util.ArrayList;
import java.util.List;

import static com.example.countryNeighborsTour.security.ApplicationUserRole.ADMIN;
import static com.example.countryNeighborsTour.security.ApplicationUserRole.USER;

@Configuration
@EnableWebSecurity
public class ApplicationSecurityConfig extends WebSecurityConfigurerAdapter {

    private final UserRepository userRepository;

    public ApplicationSecurityConfig(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http
                .csrf()
                .disable()
                .authorizeRequests()
                .antMatchers(HttpMethod.POST, "/api/v3/users").permitAll()
                .antMatchers(HttpMethod.DELETE, "/api/v3/userById/{id}").hasRole(ADMIN.name())
                .antMatchers(HttpMethod.GET, "/api/v3/allUsers").hasRole(ADMIN.name())
                .antMatchers("/api/v3/status").permitAll()
                .antMatchers(HttpMethod.POST, "/api/v4/country/{name}/{abrev}").hasRole(ADMIN.name())
                .antMatchers(HttpMethod.DELETE, "/api/v4/country/{name}").hasRole(ADMIN.name())
                .antMatchers(HttpMethod.GET, "/api/v4/result").authenticated()
                .anyRequest()
                .authenticated()
                .and()
                .httpBasic();
    }

    @Override
    @Bean
    protected UserDetailsService userDetailsService() {
        List<User> users = userRepository.findAll();
        List<UserDetails> returnUsers = new ArrayList<>();
        for (User user : users) {
            if (user.getUserName().equals("Todor")){
                returnUsers
                        .add(
                                org.springframework.security.core.userdetails.User
                                        .builder()
                                        .username(user.getUserName())
                                        .password(user.getPassword())
                                        .roles(ADMIN.name())
                                        .build());
            }else {
                returnUsers
                        .add(
                                org.springframework.security.core.userdetails.User
                                        .builder()
                                        .username(user.getUserName())
                                        .password(user.getPassword())
                                        .roles(USER.name())
                                        .build());
            }
        }
        return new InMemoryUserDetailsManager(returnUsers);
    }
}