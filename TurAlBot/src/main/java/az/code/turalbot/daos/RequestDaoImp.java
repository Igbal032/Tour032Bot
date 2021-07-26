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
import org.w3c.dom.stylesheets.LinkStyle;

import javax.ws.rs.core.Request;
import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class RequestDaoImp implements RequestDAO {

    private final RequestRepo requestRepo;
    private final RequestToAgentRepo requestToAgentRepo;
    private final RequestToAgentDAO requestToAgentDAO;

    @Override
    public Requests deactivateStatus(String UUID) {
        Requests findRequest = requestRepo.getRequestsByUUID(UUID);
        if (findRequest==null){
            return null;
        }
        findRequest.setActive(false);
        findRequest.setRequestStatus(RequestStatus.STOP.toString());
        requestRepo.save(findRequest);
        deactivateAllRequestsForAgents(findRequest.getId());
        return findRequest;
    }

    public void deactivateAllRequestsForAgents(Long requestId){
        List<RequestToAgent> requestToAgentList = requestToAgentDAO.getRequestToAgentByReqId(requestId);
        if (requestToAgentList.size()==0){
            throw new RequestNotFoundException("request not found");
        }
        requestToAgentList.forEach(r->{
            r.setRequestStatus(RequestStatus.STOP.toString());
            requestToAgentRepo.save(r);
        });
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
    public Requests save(Requests requests) {
        return requestRepo.save(requests);
    }


}
