package az.code.Tour032.services;

import az.code.Tour032.Exceptions.AgentExistsException;
import az.code.Tour032.Exceptions.AgentNotFoundException;
import az.code.Tour032.daos.intergaces.AgentDAO;
import az.code.Tour032.dtos.AgentDTO;
import az.code.Tour032.enums.EmailStatus;
import az.code.Tour032.models.Agent;
import az.code.Tour032.models.Auth.Confirmation;
import az.code.Tour032.repos.ConfirmationRepo;
import az.code.Tour032.services.interfaces.AuthService;
import az.code.Tour032.utils.JavaMailUtil;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import java.time.LocalDateTime;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class AuthServiceImp implements AuthService {

    private final AgentDAO agentDAO;
    private final ConfirmationRepo confirmationRepo;
    private final JavaMailUtil javaMailUtil;
    private final AuthenticationManager authenticationManager;
    @Override
    public AgentDTO createAgent(AgentDTO agentDTO) throws MessagingException {
        ModelMapper modelMapper = new ModelMapper();
        Agent agent = modelMapper.map(agentDTO, Agent.class);
        Agent checkAgent = agentDAO.getAgentByEmail(agent.getEmail());
        if (checkAgent!=null){
            throw new AgentExistsException("This Agent Exists");
        }
        String encPassword = new BCryptPasswordEncoder().encode(agent.getPassword());
        agent.setPassword(encPassword);
        Random random = new Random();
        int random_integer = random.nextInt(50000-10000) + 10000;
        javaMailUtil.sendMail(agentDTO.getEmail()
                ,"Confirmation email"
                ,"<h3> Confirmation number: "+ random_integer +"</h3>");
        agentDAO.save(agent);
        saveToken(agent.getEmail(), random_integer,EmailStatus.REGISTER.toString(),null);
        System.out.println("success");
        return modelMapper.map(agent, AgentDTO.class);
    }


    @Override
    public void authenticate(String username, String password) throws Exception {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        } catch (DisabledException e) {
            throw new Exception("USER_DISABLED", e);
        } catch (BadCredentialsException e) {
            throw new Exception("INVALID_CREDENTIALS", e);
        }
    }
    public void saveToken(String email, int number, String emailStatus, LocalDateTime expiredDate){
        Confirmation confirmation = Confirmation.builder()
                .email(email)
                .confirmNumber(number)
                .emailStatus(emailStatus)
                .expiredDate(expiredDate)
                .build();
        System.out.println(confirmation.getConfirmNumber()+" number");
        System.out.println(confirmation.getEmail()+" email");
        confirmationRepo.save(confirmation);
    }

    @Override
    public boolean isVerify(String email) {
        Agent agent = agentDAO.getAgentByEmail(email);
        if (agent==null)
            throw new AgentNotFoundException("Agent not found!!");
        return agent.isVerify();
    }

    @Override
    public String verifyUser(String email, int confirmationNumber) {
        Agent agent = agentDAO.getAgentByEmail(email);
        if(agent==null)
            throw new AgentNotFoundException("Agent Not Found!!");
        Confirmation confirmation = confirmationRepo.getConfirmationByEmailAndConfirmNumber(email, confirmationNumber);
        if (confirmation!=null){
            agent.setVerify(true);
            agentDAO.save(agent);
            confirmationRepo.delete(confirmation);
            return "Account has confirmed successfully!!!";
        }
        return "Account had already confirmed";
    }

    @Override
    public AgentDTO checkToken(String email, String token) {
        boolean check =  confirmationRepo.existsByEmailAndConfirmNumber(email, token);
        if (check){
            Agent agent = agentDAO.getAgentByEmail(email);
            agent.setVerify(true);
            agentDAO.save(agent);
            ModelMapper modelMapper = new ModelMapper();
            AgentDTO agentDTO = modelMapper.map(agent, AgentDTO.class);
            return agentDTO;
        }
        return null;
    }

    @Override
    public String forgetPsw(String email) {
        Agent agent = agentDAO.getAgentByEmail(email);
        if(agent==null){
            throw new AgentNotFoundException("Agent Not Found!!!");
        }
        if(!agent.isVerify()){
            return "Account does not confirmed";
        }
        Random random  = new Random();
        int random_integer = random.nextInt(50000-10000) + 10000;
        javaMailUtil.sendMail(email
                ,"Reset Password"
                ,"It will be expired after 5 minutes.<p>Code for reset password: </p><h3> "+ random_integer +"</h3>");
        saveToken(email,random_integer,EmailStatus.FORGET_PASSWORD.toString(), LocalDateTime.now().plusMinutes(5l));
        return "Mail Send To Your Email!!";
    }

    @Override
    public String verifyForgetPsw(String email, int confirmationNumber, String newPassword) {
        Agent agent = agentDAO.getAgentByEmail(email);
        if(agent==null)
            throw new AgentNotFoundException("Agent Not Found!!");
        Confirmation confirmation = confirmationRepo.getConfirmationByEmailAndConfirmNumber(email,confirmationNumber);
        if (confirmation!=null){
            String encPassword = new BCryptPasswordEncoder().encode(newPassword);
            agent.setPassword(encPassword);
            agentDAO.save(agent);
            confirmationRepo.delete(confirmation);
            return "password changed successfully!!!";
        }
        return "wrong number";
    }

    @Override
    public String changePassword(Agent agent, String oldPassword, String newPassword) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        boolean isTheSameWithOldPsw = encoder.matches(oldPassword, agent.getPassword());
        if(!isTheSameWithOldPsw){
            return "Wrong Old Password";
        }
        boolean isTheSameWithNewPsw = encoder.matches(newPassword, agent.getPassword());
        if(isTheSameWithNewPsw){
            return "The same Password";
        }
        Agent findAgent = agentDAO.getAgentByEmail(agent.getEmail());
        String encNewPassword = new BCryptPasswordEncoder().encode(newPassword);
        findAgent.setPassword(encNewPassword);
        agentDAO.save(findAgent);
        return "password changed successfully!!!";
    }
}
