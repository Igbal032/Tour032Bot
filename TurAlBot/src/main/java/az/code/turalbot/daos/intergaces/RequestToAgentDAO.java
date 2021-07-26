package az.code.turalbot.daos.intergaces;

import az.code.turalbot.models.Agent;
import az.code.turalbot.models.RequestToAgent;
import az.code.turalbot.models.Requests;

import java.util.List;

public interface RequestToAgentDAO {
    void saveRequestForPerAgent(Requests requests);

    List<RequestToAgent> getRequestToAgentByReqId(Long reqId);

    RequestToAgent getRequestByAgentIdAndRequestId(long agentId, long requestId);

    List<RequestToAgent> getRequestsBasedOnAgentAndStatus(Agent agent, String status);

    RequestToAgent save(RequestToAgent requestToAgent);
 }
