package com.example.supermarket.supermarketsheepserver.controller;

import com.example.supermarket.supermarketsheepserver.request.BillRequest;
import com.example.supermarket.supermarketsheepserver.response.BillResponse;
import com.example.supermarket.supermarketsheepserver.service.BillService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/bill")
@RequiredArgsConstructor
public class BillRest {

    private final BillService billService;

    @PostMapping("/thanhtoantructiep")
    public ResponseEntity<?> createBill(@Valid @RequestBody BillRequest request) {
        try {
            BillResponse response = billService.createBill(request);
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (IllegalStateException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>("An unexpected error occurred: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}