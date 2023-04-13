package com.example.countryNeighborsTour.model;

import javax.persistence.*;
import javax.validation.constraints.Pattern;

@Entity(name = "User")
@Table(name = "user")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId;

    @Column(name = "user_name",
            nullable = false,
            columnDefinition = "TEXT")
    private String userName;

    @Column(name = "password",
            nullable = false,
            columnDefinition = "TEXT")
    private String password;

    @Column(name = "is_online",
            nullable = false,
            columnDefinition = "INT")
    private Integer isOnline = 0;

    public User() {
    }

    public User(String userName, String password) {
        this.userName = userName;
        this.password = password;
    }

    public Long getUserId() {
        return userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Integer getIsOnline() {
        return isOnline;
    }

    public void setIsOnline(Integer isOnline) {
        this.isOnline = isOnline;
    }
}