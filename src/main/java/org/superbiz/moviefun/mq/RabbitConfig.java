package org.superbiz.moviefun.mq;

import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.dsl.amqp.Amqp;
import org.superbiz.moviefun.albums.AlbumsUpdateMessageConsumer;

@Configuration
public class RabbitConfig {

    @Value("${rabbitmq.uri}") String rabbitMQURI;
    @Value("${rabbitmq.queue}") String rabbitMQ;
    public ConnectionFactory connectionFactory(){
        CachingConnectionFactory ccf = new CachingConnectionFactory();
        ccf.setUri(rabbitMQURI);
        return ccf;
    }

    @Bean
    public IntegrationFlow amqpInbound(ConnectionFactory connectionFactory, AlbumsUpdateMessageConsumer consumer) {
        return IntegrationFlows
                .from(Amqp.inboundAdapter(connectionFactory, rabbitMQ))
                .handle(consumer::consume)
                .get();
    }
}
