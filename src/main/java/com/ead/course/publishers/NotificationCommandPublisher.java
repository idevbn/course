package com.ead.course.publishers;

import com.ead.course.dtos.NotificationCommandDTO;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class NotificationCommandPublisher {

    @Value("${ead.broker.exchange.notificationCommandExchange}")
    private String notificationCommandExchange;

    @Value("${ead.broker.key.notificationCommandKey}")
    private String notificationCommandKey;

    private final RabbitTemplate rabbitTemplate;

    public NotificationCommandPublisher(final RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void publishNotificationCommand(final NotificationCommandDTO notificationCommandDTO) {
        this.rabbitTemplate.convertAndSend(
                this.notificationCommandExchange,
                this.notificationCommandKey,
                notificationCommandDTO
        );
    }

}
