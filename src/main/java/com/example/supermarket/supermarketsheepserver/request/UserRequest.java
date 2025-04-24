package com.example.supermarket.supermarketsheepserver.request;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Setter @Getter
public class UserRequest {
    private Long id;
    private String code;
    private String name;
    private String phone;
    private String email;
    private Date dateOfBirth;
    private Integer gender;
    private Integer status;
    private String image;
    private boolean enabled;
    private String roleName;
    private String username;
    private Long accountId;
}
