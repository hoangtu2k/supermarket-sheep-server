package com.example.supermarket.supermarketsheepserver.request;

import lombok.Getter;
import lombok.Setter;

@Setter @Getter
public class AccountRequest {
    private  Long id;
    private String username;
    private String password;
    private Integer status;
    private String roleName;
}
