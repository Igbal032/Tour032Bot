package az.code.turalbot.daos.intergaces;

import az.code.turalbot.models.Agent;

public interface AgentDAO {
    Agent save(Agent agent);
    Agent getAgentByEmail(String email);
}
