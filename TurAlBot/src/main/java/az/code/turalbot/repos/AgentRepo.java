package az.code.turalbot.repos;

import az.code.turalbot.models.Agent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface AgentRepo extends JpaRepository<Agent, Long> {

    @Query("select a from Agent a where a.email=:email")
    Agent getAgentByAgentEmail(String email);
}
