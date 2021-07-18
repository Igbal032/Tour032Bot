package az.code.turalbot.config;

import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

//    @Value("${sample.rabbitmq.exchange}")
//    String exchange;
//    @Value("${sample.rabbitmq.queue}")
//    String senderQueue;
//    @Value("${sample.rabbitmq.routingKey}")
//    String senderKey;


    @Bean
    TopicExchange exchange() {
        return new TopicExchange("default");
    }


    @Bean
    Queue senderQueue(){
        return new Queue("senderQueue", true);
    }

    @Bean
    Binding senderBinding(Queue senderQueue, TopicExchange exchange){
        return BindingBuilder.bind(senderQueue).to(exchange).with("senderKey");
    }

//    @Bean
//    TopicExchange exchange2() {
//        return new TopicExchange("default2");
//    }

//    @Bean
//    Queue receiverQueue(){
//        return new Queue("receiveQueue", true);
//    }
//
//    @Bean
//    Binding receiverBinding(Queue senderQueue, TopicExchange exchange){
//        return BindingBuilder.bind(senderQueue).to(exchange).with("receiveKey");
//    }

    @Bean
    public MessageConverter jsonMessageConverter(){
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public AmqpTemplate template(ConnectionFactory connectionFactory){
        final RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(jsonMessageConverter());
        return rabbitTemplate;
    }
}
