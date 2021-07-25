package az.code.turalbot.services.interfaces;

import az.code.turalbot.dtos.AgentDTO;
import az.code.turalbot.models.Agent;

public interface AuthService {
    AgentDTO createAgent(AgentDTO agentDTO);
    boolean isVerify(String email);
    AgentDTO checkToken(String email, String token);
}
