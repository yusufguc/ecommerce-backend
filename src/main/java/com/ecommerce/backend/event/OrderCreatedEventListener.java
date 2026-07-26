package com.ecommerce.backend.event;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
public class OrderCreatedEventListener {

    private static final String TOPIC = "order-events";

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public OrderCreatedEventListener(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onOrderCreated(OrderCreatedEvent event) {
        log.info("Publishing OrderCreatedEvent to Kafka: orderId={}", event.orderId());
        kafkaTemplate.send(TOPIC, event.orderId().toString(), event);
    }
}
