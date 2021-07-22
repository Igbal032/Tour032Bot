package az.code.turalbot.daos;

import az.code.turalbot.Exceptions.AgentNotFoundException;
import az.code.turalbot.daos.intergaces.AgentDAO;
import az.code.turalbot.models.Agent;
import az.code.turalbot.repos.AgentRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AgentDaoImp implements AgentDAO {

    private final AgentRepo agentRepo;

    @Override
    public Agent save(Agent agent) {
        if(agent==null){
            throw new AgentNotFoundException("Wrong Information");
        }
        return agentRepo.save(agent);
    }

    @Override
    public Agent getAgentByEmail(String email) {
        Agent agent = agentRepo.getAgentByAgentEmail(email);
        return agent;
    }
}
