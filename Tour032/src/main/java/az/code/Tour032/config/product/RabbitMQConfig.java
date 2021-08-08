package az.code.Tour032.config.product;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.net.URI;
import java.net.URISyntaxException;

@Profile("!dev")
@Configuration
public class RabbitMQConfig {

    @Bean
    TopicExchange exchange() {
        return new TopicExchange("default");
    }
    @Bean
    Queue offerQueue(){
        return new Queue("offerQueue", true);
    }
    @Bean
    Queue requestQueue2(){
        return new Queue("requestQueue", true);
    }
    @Bean
    Queue stopQueue(){
        return new Queue("stopQueue", true);
    }
    @Bean
    Queue acceptQueue(){
        return new Queue("acceptQueue", true);
    }

    @Bean
    Binding offerBinding(@Qualifier("offerQueue") Queue offerQueue, TopicExchange exchange){
        return BindingBuilder.bind(offerQueue).to(exchange).with("offerKey");
    }

    @Bean
    Binding requestBinding(@Qualifier("requestQueue2") Queue requestQueue, TopicExchange exchange){
        return BindingBuilder.bind(requestQueue).to(exchange).with("requestKey");
    }

    @Bean
    Binding stopBinding(@Qualifier("stopQueue") Queue stopQueue, TopicExchange exchange){
        return BindingBuilder.bind(stopQueue).to(exchange).with("stopKey");
    }

    @Bean
    Binding acceptBinding(@Qualifier("acceptQueue") Queue stopQueue, TopicExchange exchange){
        return BindingBuilder.bind(stopQueue).to(exchange).with("acceptKey");
    }



    @Bean
    public Jackson2JsonMessageConverter converter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public ConnectionFactory connectionFactory() throws URISyntaxException {
        final URI rabbitMqUrl = new URI(System.getenv("CLOUDAMQP_URL"));
        final CachingConnectionFactory factory = new CachingConnectionFactory();
        factory.setUri(rabbitMqUrl);
        return factory;
    }

    @Bean
    public RabbitTemplate template() throws URISyntaxException {
        RabbitTemplate temp = new RabbitTemplate(connectionFactory());
        temp.setMessageConverter(converter());
        return temp;
    }



}
