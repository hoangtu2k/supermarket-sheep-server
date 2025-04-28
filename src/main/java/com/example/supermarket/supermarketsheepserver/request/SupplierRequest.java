package com.example.supermarket.supermarketsheepserver.request;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class SupplierRequest {

    private Long id;
    private String code;
    private String name;
    private String phone;
    private String email;
    private String address;

}
