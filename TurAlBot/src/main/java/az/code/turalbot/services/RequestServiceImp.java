package az.code.turalbot.services;

import az.code.turalbot.daos.intergaces.RequestDAO;
import az.code.turalbot.dtos.OfferDTO;
import az.code.turalbot.models.Requests;
import az.code.turalbot.services.interfaces.RequestService;
import az.code.turalbot.services.interfaces.TurAlBotService;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.*;
import java.io.IOException;

@Service
@RequiredArgsConstructor
public class RequestServiceImp implements RequestService {

    private final RequestDAO requestDAO;
    private final RabbitTemplate template;
    private final TopicExchange exchange;
    private final TurAlBotService turAlBotService;
    @Value("${sample.rabbitmq.senderKey}")
    String senderKey;
    @Override
    public Requests getRequestWithUUID(String UUID) {
        return requestDAO.getWithUUID(UUID);
    }

    @Override
    public ResponseBuilder sendDataToRabBitMQ(String UUID, MultipartFile file) throws IOException {
        Requests requests = requestDAO.getWithUUID(UUID);
        OfferDTO offer = OfferDTO.builder()
                .UUID(UUID)
                .chatId(requests.getChatId())
                .file(file.getBytes())
                .build();
        template.convertAndSend(exchange.getName(),senderKey,offer);
        return Response.ok();
    }
}
