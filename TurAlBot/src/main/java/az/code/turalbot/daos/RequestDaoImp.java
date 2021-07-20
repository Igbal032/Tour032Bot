package az.code.turalbot.daos;

import az.code.turalbot.Exceptions.RequestNotFoundException;
import az.code.turalbot.daos.intergaces.RequestDAO;
import az.code.turalbot.daos.intergaces.RequestToAgentDAO;
import az.code.turalbot.enums.RequestStatus;
import az.code.turalbot.models.Agent;
import az.code.turalbot.models.RequestToAgent;
import az.code.turalbot.models.Requests;
import az.code.turalbot.repos.AgentRepo;
import az.code.turalbot.repos.RequestRepo;
import az.code.turalbot.repos.RequestToAgentRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class RequestDaoImp implements RequestDAO {

    private final RequestRepo requestRepo;
    private final RequestToAgentDAO requestToAgentDAO;
    private final RabbitTemplate template2;
    private final TopicExchange exchange2;
//    @Value("${sample.rabbitmq.requestKey}")
//    String requestKey;

    @Override
    public Requests deactivateStatus(String UUID) {
        Requests findRequest = requestRepo.getRequestsByUUID(UUID);
        findRequest.setActive(false);
        requestRepo.save(findRequest);


        return findRequest;
    }

    @Override
    public Requests getWithUUID(String UUID) {
        Requests request   = requestRepo.getRequestsByUUID(UUID);
        if (request==null){
            throw new RequestNotFoundException("Request not found");
        }
        return request;
    }

    @Override
    public Requests saveRequest(Long chatId, String jsonText,String UUID) {
        Requests newRequests = Requests.builder()
                .UUID(UUID).chatId(chatId)
                .isActive(true).jsonText(jsonText)
                .requestStatus(RequestStatus.ACTIVE.toString())
                .createdDate(LocalDateTime.now()).build();
        Requests request = requestRepo.save(newRequests);
        requestToAgentDAO.saveRequestForPerAgent(request);
//        template2.convertAndSend("default2","requestKey",newRequests);
        return newRequests;
    }
}
