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
//    @Value("${sample.rabbitmq.offerQueue}")
//    String offerQueue;
//    @Value("${sample.rabbitmq.offerKey}")
//    String offerKey;


    @Bean
    TopicExchange exchange() {
        return new TopicExchange("default");
    }


    @Bean
    Queue senderQueue(){
        return new Queue("offerQueue", true);
    }

    @Bean
    Binding senderBinding(Queue senderQueue, TopicExchange exchange){
        return BindingBuilder.bind(senderQueue).to(exchange).with("offerKey");
    }
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



//    @Bean
//    TopicExchange exchange2() {
//        return new TopicExchange("default2");
//    }
//    @Bean
//    Queue requestQueue(){
//        return new Queue("requestQueue", true);
//    }
//
//    @Bean
//    Binding requestBinding(Queue senderQueue, TopicExchange exchange){
//        return BindingBuilder.bind(senderQueue).to(exchange).with("requestKey");
//    }
//    @Bean
//    public MessageConverter jsonMessageConverter2(){
//        return new Jackson2JsonMessageConverter();
//    }
//    @Bean
//    public AmqpTemplate template2(ConnectionFactory connectionFactory){
//        final RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
//        rabbitTemplate.setMessageConverter(jsonMessageConverter2());
//        return rabbitTemplate;
//    }
}
