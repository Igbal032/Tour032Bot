package az.code.turalbot.repos;

import az.code.turalbot.models.Agent;
import az.code.turalbot.models.RequestToAgent;
import az.code.turalbot.models.Requests;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface RequestToAgentRepo extends JpaRepository<RequestToAgent, Long> {


    RequestToAgent getRequestToAgentByAgentAndRequests(Agent agent, Requests requests);

    @Query("select r from RequestToAgent r where r.agent.id=:agentId and r.requests.id=:reqId")
    RequestToAgent getRequestToAgentByAgIdAndReqId(Long agentId, Long reqId);

    @Query("select r from RequestToAgent r where r.requests.id=:reqId")
    List<RequestToAgent> getRequestToAgentByReqId(Long reqId);

    @Query("select r from RequestToAgent r where r.agent=:agent and r.requestStatus=:status")
    List<RequestToAgent> getRequestByAgentAndStatus(Agent agent, String status);

    @Query("select r from RequestToAgent r where r.agent=:agent and r.isArchive=:isArch")
    List<RequestToAgent> getRequestByAgentAndArchive(Agent agent, boolean isArch);

}
