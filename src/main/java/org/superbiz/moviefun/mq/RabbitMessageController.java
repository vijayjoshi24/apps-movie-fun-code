package org.superbiz.moviefun.mq;

import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class RabbitMessageController {

    private RabbitTemplate rabbitTemplate;
    private String rabbitMQ;

    RabbitMessageController(@Value("${rabbitmq.queue}")String rabbitMQ, ConnectionFactory connectionFactory, RabbitTemplate rabbitTemplate){
        this.rabbitTemplate = new RabbitTemplate(connectionFactory);
        this.rabbitMQ = rabbitMQ;
    }

    @PostMapping("/rabbit")
    public Map<String,String> publish(){

        rabbitTemplate.convertAndSend(rabbitMQ,"This text message will trigger the consumer");
        Map<String,String> respMap = new HashMap<String,String>();
        respMap.put("response","This is an unrelated JSON response");
        return respMap;

    }
}

