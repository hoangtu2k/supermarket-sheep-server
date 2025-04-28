package com.example.supermarket.supermarketsheepserver.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;


import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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

    @ManyToOne
    @JoinColumn(name = "role_id")
    private Role role;

}
