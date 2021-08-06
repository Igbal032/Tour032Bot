package az.code.turalbot.daos;

import az.code.turalbot.daos.intergaces.RequestToAgentDAO;
import az.code.turalbot.enums.RequestStatus;
import az.code.turalbot.models.Agent;
import az.code.turalbot.models.RequestToAgent;
import az.code.turalbot.models.Requests;
import az.code.turalbot.repos.AgentRepo;
import az.code.turalbot.repos.RequestToAgentRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class RequestToAgentDaoImpl implements RequestToAgentDAO {

    private final AgentRepo agentRepo;
    private final RequestToAgentRepo requestToAgentRepo;

    @Override
    public void saveRequestForPerAgent(Requests requests) {
        List<Agent> agentList = agentRepo.findAll();
        agentList.stream().parallel().forEach(agent -> {
            RequestToAgent requestToAgent = RequestToAgent.builder()
                    .agent(agent)
                    .requestStatus(RequestStatus.ACTIVE.toString())
                    .requests(requests)
                    .build();
            requestToAgentRepo.save(requestToAgent);
        });
    }

    @Override
    public List<RequestToAgent> getRequestToAgentByReqId(Long reqId) {
        return requestToAgentRepo.getRequestToAgentByReqId(reqId);
    }

    @Override
    public RequestToAgent getRequestByAgentIdAndRequestId(long agentId, long requestId) {
        RequestToAgent requestToAgent = requestToAgentRepo.getRequestToAgentByAgIdAndReqId(agentId,requestId);
        return requestToAgent;
    }

    @Override
    public List<RequestToAgent> getRequestsBasedOnAgentAndStatus(Agent agent, String status) {
        return requestToAgentRepo.getRequestByAgentAndStatus(agent,status);
    }

    @Override
    public List<RequestToAgent> getRequestsOnArchive(Agent agent) {
        return requestToAgentRepo.getRequestByAgentAndArchive(agent, true);
    }


    @Override
    public RequestToAgent save(RequestToAgent requestToAgent) {
        return requestToAgentRepo.save(requestToAgent);
    }
}
