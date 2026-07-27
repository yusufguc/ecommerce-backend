package com.ecommerce.backend.service.impl;

import com.ecommerce.backend.dto.request.OrderItemRequest;
import com.ecommerce.backend.dto.request.OrderRequest;
import com.ecommerce.backend.dto.response.OrderResponse;
import com.ecommerce.backend.event.OrderCreatedEvent;
import com.ecommerce.backend.exception.base.BaseException;
import com.ecommerce.backend.exception.message.MessageType;
import com.ecommerce.backend.mapper.OrderMapper;
import com.ecommerce.backend.model.Order;
import com.ecommerce.backend.model.OrderItem;
import com.ecommerce.backend.model.Product;
import com.ecommerce.backend.model.enums.OrderStatus;
import com.ecommerce.backend.model.enums.Role;
import com.ecommerce.backend.repository.OrderRepository;
import com.ecommerce.backend.security.UserPrincipal;
import com.ecommerce.backend.service.OrderService;
import com.ecommerce.backend.service.ProductService;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final ProductService productService;
    private final OrderMapper orderMapper;
    private final ApplicationEventPublisher eventPublisher;

    public OrderServiceImpl(OrderRepository orderRepository,
                             ProductService productService,
                             OrderMapper orderMapper,
                             ApplicationEventPublisher eventPublisher) {
        this.orderRepository = orderRepository;
        this.productService = productService;
        this.orderMapper = orderMapper;
        this.eventPublisher = eventPublisher;
    }

    @Override
    @Transactional
    public OrderResponse create(UserPrincipal principal, OrderRequest request) {
        Order order = new Order();
        order.setUser(principal.getUser());
        order.setStatus(OrderStatus.PENDING);

        BigDecimal total = BigDecimal.ZERO;
        for (OrderItemRequest itemRequest : request.getItems()) {
            Product product = productService.getProductEntity(itemRequest.getProductId());
            productService.adjustStock(itemRequest.getProductId(), -itemRequest.getQuantity());

            OrderItem item = new OrderItem();
            item.setProduct(product);
            item.setProductName(product.getName());
            item.setUnitPrice(product.getPrice());
            item.setQuantity(itemRequest.getQuantity());
            order.addItem(item);

            total = total.add(product.getPrice().multiply(BigDecimal.valueOf(itemRequest.getQuantity())));
        }
        order.setTotalAmount(total);

        orderRepository.save(order);
        eventPublisher.publishEvent(toOrderCreatedEvent(order));
        return orderMapper.toResponse(order);
    }

    private OrderCreatedEvent toOrderCreatedEvent(Order order) {
        List<OrderCreatedEvent.Item> items = order.getItems().stream()
                .map(item -> new OrderCreatedEvent.Item(
                        item.getProduct().getId(),
                        item.getProductName(),
                        item.getQuantity(),
                        item.getUnitPrice()))
                .toList();

        return new OrderCreatedEvent(
                order.getId(),
                order.getUser().getId(),
                order.getUser().getEmail(),
                items,
                order.getTotalAmount(),
                order.getCreatedAt());
    }

    @Override
    @Transactional(readOnly = true)
    public OrderResponse getById(UserPrincipal principal, Long id) {
        return orderMapper.toResponse(findOwnedOrderOrThrow(principal, id));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OrderResponse> getMyOrders(UserPrincipal principal, Pageable pageable) {
        return orderRepository.findByUserId(principal.getUser().getId(), pageable)
                .map(orderMapper::toResponse);
    }

    @Override
    @Transactional
    public OrderResponse updateStatus(Long id, OrderStatus status) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new BaseException(MessageType.ORDER_NOT_FOUND));
        order.setStatus(status);
        return orderMapper.toResponse(order);
    }

    private Order findOwnedOrderOrThrow(UserPrincipal principal, Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new BaseException(MessageType.ORDER_NOT_FOUND));

        boolean isOwner = order.getUser().getId().equals(principal.getUser().getId());
        boolean isAdmin = principal.getUser().getRole() == Role.ADMIN;
        if (!isOwner && !isAdmin) {
            throw new BaseException(MessageType.ORDER_NOT_FOUND);
        }
        return order;
    }
}
