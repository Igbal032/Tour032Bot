package az.code.turalbot.services;

import az.code.turalbot.Exceptions.RequestNotFoundException;
import az.code.turalbot.daos.intergaces.OfferDAO;
import az.code.turalbot.daos.intergaces.RequestDAO;
import az.code.turalbot.daos.intergaces.RequestToAgentDAO;
import az.code.turalbot.dtos.ImageDTO;
import az.code.turalbot.dtos.OfferDTO;
import az.code.turalbot.enums.RequestStatus;
import az.code.turalbot.models.Agent;
import az.code.turalbot.models.Offer;
import az.code.turalbot.models.Requests;
import az.code.turalbot.services.interfaces.RequestService;
import az.code.turalbot.services.interfaces.TurAlBotService;
import az.code.turalbot.utils.Utils;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.annotation.RabbitListeners;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class RequestServiceImp implements RequestService {

    private final RequestDAO requestDAO;
    private final RequestToAgentDAO requestToAgentDAO;
    private final RabbitTemplate template;
    private final TopicExchange exchange;
    @Value("${sample.rabbitmq.requestKey}")
    String requestKey;
    @Value("${sample.rabbitmq.stopKey}")
    String stopKey;
    @Override
    public Requests getRequestWithUUID(String UUID) {
        return requestDAO.getWithUUID(UUID);
    }

    @Override
    public void sendRequestToRabbitMQ(Long chatId, String jsonText,String UUID) {
        Requests newRequests = Requests.builder()
                .UUID(UUID).chatId(chatId)
                .isActive(true).jsonText(jsonText)
                .requestStatus(RequestStatus.ACTIVE.toString())
                .build();
        template.convertAndSend(exchange.getName(),requestKey,newRequests);
        System.out.println("Sent to Request Queue");
    }

    @Override
    @RabbitListener(queues = "requestQueue")
    public void listenRequestFromRabbitMQ(Requests request) {
        request.setCreatedDate(LocalDateTime.now());
        Requests savedRequest = requestDAO.save(request);
        requestToAgentDAO.saveRequestForPerAgent(savedRequest);
        System.out.println("Save to DB");
    }

    @Override
    public void sendStopRequestToStopRabbitMQ(String UUID) {
        template.convertAndSend(exchange.getName(),stopKey,UUID);
        System.out.println("Send STOP to RABBIT MQ");
    }

    @Override
    @RabbitListener(queues = "stopQueue")
    public void listenStopRequestFromRabbitMQ(String UUID) {
        requestDAO.deactivateStatus(UUID);
        System.out.println("SAVE STOP FROM RABBIT MQ");
    }
}
