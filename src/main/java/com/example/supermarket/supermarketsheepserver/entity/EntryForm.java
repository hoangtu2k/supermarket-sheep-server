package com.example.supermarket.supermarketsheepserver.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class EntryForm {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(nullable = false)
    private LocalDateTime entryDate;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    @NotNull
    private User user;

    @ManyToOne
    @JoinColumn(name = "supplier_id", nullable = false)
    @NotNull
    private Supplier supplier;

    @NotNull
    @Column(nullable = false)
    private BigDecimal total;

    public enum EntryFormStatus {
        PENDING, COMPLETED, CANCELED
    }

    @Enumerated(EnumType.STRING)
    @NotNull
    @Column(nullable = false)
    private EntryFormStatus status;

    @OneToMany(mappedBy = "entryForm", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<EntryDetails> entryDetails;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}