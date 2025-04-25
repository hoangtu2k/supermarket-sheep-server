package com.example.supermarket.supermarketsheepserver.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class EntryDetails {

    @EmbeddedId
    private EntryDetailsId id;

    @ManyToOne
    @MapsId("entryForm")
    @JoinColumn(name = "entryform_id")
    private EntryForm entryform;

    @ManyToOne
    @MapsId("product")
    @JoinColumn(name = "product_id")
    private Product product;

    private int quantity;
    private BigDecimal import_price;
    private BigDecimal payment;

}
