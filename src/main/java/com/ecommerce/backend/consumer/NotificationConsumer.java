package com.ecommerce.backend.consumer;

import com.ecommerce.backend.event.OrderCreatedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.BackOff;
import org.springframework.kafka.annotation.DltHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class NotificationConsumer {

    @RetryableTopic(
            attempts = "4",
            backOff = @BackOff(delay = 1000, multiplier = 2.0),
            retryTopicSuffix = "-notif-retry",
            dltTopicSuffix = "-notif-dlt"
    )
    @KafkaListener(topics = "order-events", groupId = "notification-group")
    public void onOrderCreated(OrderCreatedEvent event) {
        log.info("[BİLDİRİM] Sipariş onay maili gönderildi: orderId={}, to={}, tutar={}",
                event.orderId(), event.userEmail(), event.totalAmount());
    }

    @DltHandler
    public void onDlt(OrderCreatedEvent event) {
        log.error("[DLT] Bildirim gönderilemedi, tüm denemeler tükendi: orderId={}", event.orderId());
    }
}
