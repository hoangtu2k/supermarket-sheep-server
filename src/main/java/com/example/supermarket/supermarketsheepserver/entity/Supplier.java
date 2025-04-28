package com.example.supermarket.supermarketsheepserver.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class Supplier {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String code;
    private String name;
    private String phone;
    private String email;
    private String address;
    private Integer status;

    @JsonIgnore
    @OneToMany(mappedBy = "supplier")
    private Set<Product> products = new HashSet<Product>();

}
