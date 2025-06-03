package com.example.supermarket.supermarketsheepserver.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(indexes = {@Index(columnList = "billCode")})
public class Bill {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(nullable = false, unique = true)
    private String billCode;

    @NotNull
    @Column(nullable = false)
    private LocalDateTime createdAt;

    @NotNull
    @Column(nullable = false)
    private BigDecimal totalAmount;

    public enum BillStatus {
        PAID, PENDING, CANCELED
    }

    @Enumerated(EnumType.STRING)
    @NotNull
    @Column(nullable = false)
    private BillStatus status;

    @OneToMany(mappedBy = "bill", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BillDetails> billDetails;

    private String customerName;

    private String customerEmail;

    public void calculateTotalAmount() {
        this.totalAmount = billDetails.stream()
                .map(BillDetails::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}