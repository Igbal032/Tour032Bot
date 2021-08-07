package az.code.Tour032.daos;

import az.code.Tour032.daos.intergaces.RequestToAgentDAO;
import az.code.Tour032.enums.RequestStatus;
import az.code.Tour032.models.Agent;
import az.code.Tour032.models.RequestToAgent;
import az.code.Tour032.models.Requests;
import az.code.Tour032.repos.AgentRepo;
import az.code.Tour032.repos.RequestToAgentRepo;
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
