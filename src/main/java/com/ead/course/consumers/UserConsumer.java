package com.ead.course.consumers;

import com.ead.course.dtos.UserEventDTO;
import com.ead.course.enums.ActionType;
import com.ead.course.models.UserModel;
import com.ead.course.services.UserService;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
public class UserConsumer {

    private final UserService userService;

    @Autowired
    public UserConsumer(final UserService userService) {
        this.userService = userService;
    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "${ead.broker.queue.userEventQueue.name}", durable = "true"),
            exchange = @Exchange(
                    value = "${ead.broker.exchange.userEventExchange}",
                    type = ExchangeTypes.FANOUT,
                    ignoreDeclarationExceptions = "true"
            )
    ))
    public void listenUserEvent(@Payload final UserEventDTO userEventDTO) {
        final UserModel userModel = userEventDTO.convertToUserModel();

        switch (ActionType.valueOf(userEventDTO.getActionType())) {
            case CREATE, UPDATE -> this.userService.save(userModel);
            case DELETE -> this.userService.delete(userEventDTO.getUserId());
        }
    }

}
