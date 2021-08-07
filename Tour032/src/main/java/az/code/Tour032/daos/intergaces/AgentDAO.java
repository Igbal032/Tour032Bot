package az.code.Tour032.daos.intergaces;

import az.code.Tour032.models.Agent;

public interface AgentDAO {
    Agent save(Agent agent);
    Agent getAgentByEmail(String email);
}
