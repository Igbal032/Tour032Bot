package az.code.turalbot.services;

import az.code.turalbot.Exceptions.AgentExistsException;
import az.code.turalbot.Exceptions.AgentNotFoundException;
import az.code.turalbot.daos.intergaces.AgentDAO;
import az.code.turalbot.dtos.AgentDTO;
import az.code.turalbot.models.Agent;
import az.code.turalbot.models.Auth.TokenForRegister;
import az.code.turalbot.repos.TokenForRegisterRepo;
import az.code.turalbot.services.interfaces.AuthService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImp implements AuthService {

    private final AgentDAO agentDAO;
    private final TokenForRegisterRepo tokenForRegisterRepo;

    @Override
    public AgentDTO createAgent(AgentDTO agentDTO){
        ModelMapper modelMapper = new ModelMapper();
        Agent agent = modelMapper.map(agentDTO, Agent.class);
        Agent checkAgent = agentDAO.getAgentByEmail(agent.getEmail());
        if (checkAgent!=null){
            throw new AgentExistsException("This Agent Exists");
        }
        String encPassword = new BCryptPasswordEncoder().encode(agent.getPassword());
        agent.setPassword(encPassword);
        String token = "123456";
        //Get the mailer instance
//        agentDTO.getEmail(),"Email verification", "You can verify your account" +
//                " to click the link: http://localhost:3232/api/auth/verify?"+token;
        agentDAO.save(agent);
        tokenForRegisterRepo.save(TokenForRegister.builder()
                .email(agent.getEmail())
                .token(token)
                .build());
        System.out.println("success");
        return modelMapper.map(agentDTO, AgentDTO.class);
    }

    @Override
    public boolean isVerify(String email) {
        Agent agent = agentDAO.getAgentByEmail(email);
        if (agent==null)
            throw new AgentNotFoundException("Agent not found!!");
        return agent.isVerify();
    }
}
