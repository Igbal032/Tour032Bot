package az.code.turalbot.services.interfaces;

import az.code.turalbot.dtos.AgentDTO;
import az.code.turalbot.models.Agent;

import javax.mail.MessagingException;

public interface AuthService {
    AgentDTO createAgent(AgentDTO agentDTO) throws MessagingException;
    boolean isVerify(String email);
    String verifyUser(String email, int confirmationNumber);
    AgentDTO checkToken(String email, String token);
    String forgetPsw(String email);
    String verifyForgetPsw(String email, int confirmationNumber,String newPassword);
    String changePassword(Agent agent, String oldPassword, String newPassword);
}
