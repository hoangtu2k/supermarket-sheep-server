package com.example.supermarket.supermarketsheepserver.request;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Setter @Getter
public class UserRequest {
    private  Long id;
    private String code;
    private String name;
    private String username;
    private String password;
    private String phone;
    private String email;
    private Date dateOfBirth;
    private String address;
    private Integer status;
    private String roleName;
    private Long accountId;
}
