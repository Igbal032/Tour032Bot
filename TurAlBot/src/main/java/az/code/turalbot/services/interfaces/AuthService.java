package az.code.turalbot.services.interfaces;

import az.code.turalbot.dtos.AgentDTO;
import org.springframework.stereotype.Service;

public interface AuthService {
    AgentDTO createAgent(AgentDTO agentDTO);
    boolean isVerify(String email);
}
