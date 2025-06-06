package com.example.supermarket.supermarketsheepserver.request;

import lombok.Builder;
import lombok.Data;

@Data
public class UnitRequest {

    private Long id;
    private String name;
    private String status;

}
