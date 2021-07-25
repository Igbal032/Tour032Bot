package az.code.turalbot.services;

import az.code.turalbot.Exceptions.AgentExistsException;
import az.code.turalbot.Exceptions.AgentNotFoundException;
import az.code.turalbot.daos.intergaces.AgentDAO;
import az.code.turalbot.dtos.AgentDTO;
import az.code.turalbot.models.Agent;
import az.code.turalbot.models.Auth.Confirmation;
import az.code.turalbot.repos.TokenForRegisterRepo;
import az.code.turalbot.services.interfaces.AuthService;
import az.code.turalbot.utils.MailSenderTest;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImp implements AuthService {

    private final AgentDAO agentDAO;
    private final TokenForRegisterRepo tokenForRegisterRepo;
    private final MailSenderTest mailSenderTest;
    @Value("${message.content}")
    String msjContent;


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
        mailSenderTest.sendEmail(agentDTO.getEmail(),"Confirmation Email",msjContent+token+"?email="+agentDTO.getEmail());
        agentDAO.save(agent);
        tokenForRegisterRepo.save(Confirmation.builder()
                .email(agent.getEmail())
                .token(token)
                .build());
        System.out.println("success");
        return modelMapper.map(agent, AgentDTO.class);
    }

    @Override
    public boolean isVerify(String email) {
        Agent agent = agentDAO.getAgentByEmail(email);
        if (agent==null)
            throw new AgentNotFoundException("Agent not found!!");
        return agent.isVerify();
    }

    @Override
    public AgentDTO checkToken(String email, String token) {
        boolean check =  tokenForRegisterRepo.existsByEmailAndToken(email, token);
        if (check){
            Agent agent = agentDAO.getAgentByEmail(email);
            agent.setVerify(true);
            agentDAO.save(agent);
            ModelMapper modelMapper = new ModelMapper();
            AgentDTO agentDTO = modelMapper.map(agent, AgentDTO.class);
            System.out.println("checked");
            return agentDTO;
        }
        System.out.println(null+" sds");
        return null;
    }
}
