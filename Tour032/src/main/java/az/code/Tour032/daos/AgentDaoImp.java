package az.code.Tour032.daos;

import az.code.Tour032.Exceptions.AgentNotFoundException;
import az.code.Tour032.daos.intergaces.AgentDAO;
import az.code.Tour032.models.Agent;
import az.code.Tour032.repos.AgentRepo;
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
