package com.ecommerce.backend.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductStockUpdateRequest {

    @NotNull(message = "Stok değişim miktarı boş olamaz")
    private Integer quantityChange;
}
