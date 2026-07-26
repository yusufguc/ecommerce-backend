package com.ecommerce.backend.event;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record OrderCreatedEvent(
        Long orderId,
        Long userId,
        String userEmail,
        List<Item> items,
        BigDecimal totalAmount,
        LocalDateTime createdAt
) {
    public record Item(Long productId, String productName, Integer quantity, BigDecimal unitPrice) {
    }
}
