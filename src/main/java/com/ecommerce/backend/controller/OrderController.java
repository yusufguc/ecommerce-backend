package com.ecommerce.backend.controller;

import com.ecommerce.backend.dto.request.OrderRequest;
import com.ecommerce.backend.dto.request.OrderStatusUpdateRequest;
import com.ecommerce.backend.dto.response.OrderResponse;
import com.ecommerce.backend.security.UserPrincipal;
import com.ecommerce.backend.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/orders")
@Tag(name = "Order", description = "Sipariş oluşturma ve görüntüleme (tüm uçlar giriş gerektirir)")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public ResponseEntity<OrderResponse> create(@AuthenticationPrincipal UserPrincipal principal,
                                                 @Valid @RequestBody OrderRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(orderService.create(principal, request));
    }

    @GetMapping
    public ResponseEntity<Page<OrderResponse>> getMyOrders(@AuthenticationPrincipal UserPrincipal principal,
                                                             @PageableDefault(size = 20, sort = "id") Pageable pageable) {
        return ResponseEntity.ok(orderService.getMyOrders(principal, pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Sipariş detayını getir",
            description = "ADMIN herhangi bir siparişi görebilir, USER sadece kendi siparişini. " +
                    "Başkasına ait siparişe erişim denemesi, siparişin var/yok olduğunu sızdırmamak için 403 yerine 404 döner.")
    public ResponseEntity<OrderResponse> getById(@AuthenticationPrincipal UserPrincipal principal,
                                                  @PathVariable Long id) {
        return ResponseEntity.ok(orderService.getById(principal, id));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<OrderResponse> updateStatus(@PathVariable Long id,
                                                        @Valid @RequestBody OrderStatusUpdateRequest request) {
        return ResponseEntity.ok(orderService.updateStatus(id, request.getStatus()));
    }
}
