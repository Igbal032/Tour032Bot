package az.code.Tour032.daos.intergaces;

import az.code.Tour032.models.Agent;
import az.code.Tour032.models.RequestToAgent;
import az.code.Tour032.models.Requests;

import java.util.List;

public interface RequestToAgentDAO {
    void saveRequestForPerAgent(Requests requests);

    List<RequestToAgent> getRequestToAgentByReqId(Long reqId);

    RequestToAgent getRequestByAgentIdAndRequestId(long agentId, long requestId);

    List<RequestToAgent> getRequestsBasedOnAgentAndStatus(Agent agent, String status);

    List<RequestToAgent> getRequestsOnArchive(Agent agent);

    RequestToAgent save(RequestToAgent requestToAgent);
 }
