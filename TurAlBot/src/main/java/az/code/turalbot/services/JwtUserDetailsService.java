package az.code.turalbot.services;

import java.util.ArrayList;

import az.code.turalbot.models.Agent;
import az.code.turalbot.repos.AgentRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class JwtUserDetailsService implements UserDetailsService {

    private final AgentRepo agentRepo;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Agent agent   =  agentRepo.getAgentByAgentEmail(email);
        System.out.println(agent.getName());
        if (agent!=null){
            return new User(email, agent.getPassword(),
                    new ArrayList<>());
        }
        else {
            throw new UsernameNotFoundException("User not found with username: " + email);
        }
    }
}