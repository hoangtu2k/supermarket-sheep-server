package com.example.supermarket.supermarketsheepserver.service;

import com.example.supermarket.supermarketsheepserver.repository.UnitRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UnitService {

    private final UnitRepository unitRepository;

}
