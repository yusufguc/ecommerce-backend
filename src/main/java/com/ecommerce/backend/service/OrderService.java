package com.ecommerce.backend.service;

import com.ecommerce.backend.dto.request.OrderRequest;
import com.ecommerce.backend.dto.response.OrderResponse;
import com.ecommerce.backend.model.enums.OrderStatus;
import com.ecommerce.backend.security.UserPrincipal;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface OrderService {

    OrderResponse create(UserPrincipal principal, OrderRequest request);

    OrderResponse getById(UserPrincipal principal, Long id);

    Page<OrderResponse> getMyOrders(UserPrincipal principal, Pageable pageable);

    OrderResponse updateStatus(Long id, OrderStatus status);
}
