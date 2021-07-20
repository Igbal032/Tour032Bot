package az.code.turalbot.repos;

import az.code.turalbot.models.Agent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AgentRepo extends JpaRepository<Agent, Long> {
}
