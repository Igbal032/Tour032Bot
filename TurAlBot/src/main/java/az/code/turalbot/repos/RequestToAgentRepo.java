package az.code.turalbot.repos;

import az.code.turalbot.models.Agent;
import az.code.turalbot.models.RequestToAgent;
import az.code.turalbot.models.Requests;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface RequestToAgentRepo extends JpaRepository<RequestToAgent, Long> {

    RequestToAgent getRequestToAgentByAgentAndRequests(Agent agent, Requests requests);
    @Query("select r from RequestToAgent r where r.requests.id=:reqId")
    List<RequestToAgent> getRequestToAgentByReqId(Long reqId);
}
