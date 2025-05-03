package com.example.supermarket.supermarketsheepserver.controller;


import com.example.supermarket.supermarketsheepserver.request.ProductRequest;
import com.example.supermarket.supermarketsheepserver.service.ProductPhotoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/product/image")
public class ProductPhotoRest {

    @Autowired
    private ProductPhotoService service;

    @PostMapping
    public ResponseEntity<?> add(@RequestBody ProductRequest image) {
        return ResponseEntity.ok(service.createProductPhoto(image));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable("id") Long id) {
        service.deleteImg(id);
        return ResponseEntity.ok(HttpStatus.OK);
    }
}