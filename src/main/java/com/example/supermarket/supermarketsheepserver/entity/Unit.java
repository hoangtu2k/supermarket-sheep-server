package com.example.supermarket.supermarketsheepserver.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Unit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(unique = true)
    private String name; // Ví dụ: CAN, PACK, CASE

    public enum UnitStatus {
        ACTIVE, INACTIVE
    }

    @Enumerated(EnumType.STRING)
    @NotNull
    @Column(nullable = false)
    private UnitStatus status;
}
