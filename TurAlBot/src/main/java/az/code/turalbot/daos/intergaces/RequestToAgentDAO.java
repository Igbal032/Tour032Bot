package az.code.turalbot.daos.intergaces;

import az.code.turalbot.models.RequestToAgent;
import az.code.turalbot.models.Requests;

import java.util.List;

public interface RequestToAgentDAO {
    void saveRequestForPerAgent(Requests requests);

    List<RequestToAgent> getRequestToAgentByReqId(Long reqId);
 }
