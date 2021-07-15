package az.code.turalbot.services;

import az.code.turalbot.daos.RequestDAO;
import az.code.turalbot.models.Offer;
import az.code.turalbot.models.Requests;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.*;
import java.io.IOException;

@Service
@RequiredArgsConstructor
public class RequestServiceImp implements RequestService{

    private final RequestDAO requestDAO;
    private final RabbitTemplate template;
    private final TopicExchange exchange;
    private final TurAlBotService turAlBotService;
//    @Value("${sample.rabbitmq.queue}")
//    String senderQueue;
//    @Value("${sample.rabbitmq.routingKey}")
//    String senderKey;
    @Override
    public Requests getRequestWithUUID(String UUID) {
        return requestDAO.getWithUUID(UUID);
    }

    @Override
    public ResponseBuilder sendDataToRabBitMQ(String UUID, MultipartFile file) throws IOException {
        Requests requests = requestDAO.getWithUUID(UUID);
        Offer offer = Offer.builder()
                .chatId(requests.getChatId())
                .file(file.getBytes())
                .build();
        System.out.println("hazir");
        template.convertAndSend("default","senderKey",offer); //todo
        System.out.println("kecdi");
        return Response.ok();
    }
}
