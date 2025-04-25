package com.example.supermarket.supermarketsheepserver.entity;

import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EntryDetailsId implements Serializable {
    private Long entryForm;
    private Long product;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EntryDetailsId that)) return false;
        return Objects.equals(entryForm, that.entryForm) &&
                Objects.equals(product, that.product);
    }

    @Override
    public int hashCode() {
        return Objects.hash(entryForm, product);
    }
}