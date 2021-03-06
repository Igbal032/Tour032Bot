package az.code.Tour032.services.interfaces;

import az.code.Tour032.dtos.AgentDTO;
import az.code.Tour032.models.Agent;

import javax.mail.MessagingException;

public interface AuthService {
    void authenticate(String username, String password) throws Exception;
    AgentDTO createAgent(AgentDTO agentDTO) throws MessagingException;
    boolean isVerify(String email);
    String verifyUser(String email, int confirmationNumber);
    AgentDTO checkToken(String email, String token);
    String forgetPsw(String email);
    String verifyForgetPsw(String email, int confirmationNumber,String newPassword);
    String changePassword(Agent agent, String oldPassword, String newPassword);
}
