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
                    .requestStatus(RequestStatus.WAIT.toString())
                    .requests(requests)
                    .build();
            requestToAgentRepo.save(requestToAgent);
        });
    }

    @Override
    public List<RequestToAgent> getRequestToAgentByReqId(Long reqId) {
        return requestToAgentRepo.getRequestToAgentByReqId(reqId);
    }
}
