package com.ecommerce.backend.consumer;

import com.ecommerce.backend.dto.response.ProductResponse;
import com.ecommerce.backend.event.OrderCreatedEvent;
import com.ecommerce.backend.service.ProductService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.BackOff;
import org.springframework.kafka.annotation.DltHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class StockMonitorConsumer {

    private static final int LOW_STOCK_THRESHOLD = 5;

    private final ProductService productService;

    public StockMonitorConsumer(ProductService productService) {
        this.productService = productService;
    }

    @RetryableTopic(
            attempts = "4",
            backOff = @BackOff(delay = 1000, multiplier = 2.0),
            retryTopicSuffix = "-stock-retry",
            dltTopicSuffix = "-stock-dlt"
    )
    @KafkaListener(topics = "order-events", groupId = "stock-monitor-group")
    public void onOrderCreated(OrderCreatedEvent event) {
        log.info("[STOK İZLEME] orderId={} için stok seviyeleri kontrol ediliyor", event.orderId());
        for (OrderCreatedEvent.Item item : event.items()) {
            ProductResponse product = productService.getById(item.productId());
            if (product.getStockQuantity() <= LOW_STOCK_THRESHOLD) {
                log.warn("[DÜŞÜK STOK UYARISI] '{}' (id={}) stok seviyesi kritik: {}",
                        product.getName(), product.getId(), product.getStockQuantity());
            }
        }
    }

    @DltHandler
    public void onDlt(OrderCreatedEvent event) {
        log.error("[DLT] Stok izleme başarısız oldu, tüm denemeler tükendi: orderId={}", event.orderId());
    }
}
