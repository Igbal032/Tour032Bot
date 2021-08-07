package az.code.Tour032.repos;

import az.code.Tour032.models.Agent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface AgentRepo extends JpaRepository<Agent, Long> {

    @Query("select a from Agent a where a.email=:email")
    Agent getAgentByAgentEmail(String email);
}
