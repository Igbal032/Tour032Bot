package az.code.turalbot.repos;

import az.code.turalbot.models.RequestToAgent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RequestToAgentRepo extends JpaRepository<RequestToAgent, Long> {
}
