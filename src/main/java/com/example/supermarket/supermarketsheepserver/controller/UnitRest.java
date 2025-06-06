package com.example.supermarket.supermarketsheepserver.controller;

import com.example.supermarket.supermarketsheepserver.entity.Unit;
import com.example.supermarket.supermarketsheepserver.repository.UnitRepository;
import com.example.supermarket.supermarketsheepserver.service.UnitService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/units")
@RequiredArgsConstructor
public class UnitRest {

    private final UnitRepository unitRepository;

    @GetMapping
    public ResponseEntity<List<Unit>> getAllUnits() {
        return ResponseEntity.ok(unitRepository.findAll());
    }

}
