package com.example.supermarket.supermarketsheepserver.controller;

import com.example.supermarket.supermarketsheepserver.request.BillRequest;
import com.example.supermarket.supermarketsheepserver.response.BillResponse;
import com.example.supermarket.supermarketsheepserver.service.BillService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/bill")
@RequiredArgsConstructor
public class BillRest {

    private final BillService billService;
    
    @PostMapping("/thanhtoantructiep")
    @ResponseStatus(HttpStatus.CREATED)
    public BillResponse createBill(@Valid @RequestBody BillRequest request) {
        return billService.createBill(request);
    }

}
