package az.code.turalbot.controller;

import az.code.turalbot.config.JwtTokenUtil;
import az.code.turalbot.dtos.AgentDTO;
import az.code.turalbot.models.Agent;
import az.code.turalbot.models.Auth.JwtRequest;
import az.code.turalbot.models.Auth.JwtResponse;
import az.code.turalbot.services.JwtUserDetailsService;
import az.code.turalbot.services.interfaces.AuthService;
import org.apache.tomcat.jni.Local;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;

@RestController
@RequestMapping("api/auth")
public class AuthController {



    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private JwtUserDetailsService userDetailsService;

    @Autowired
    private AuthService authService;

    @RequestMapping(value = "/authenticate", method = RequestMethod.POST)
    public ResponseEntity<?> createAuthenticationToken(@RequestBody JwtRequest authenticationRequest) throws Exception {
        if (authService.isVerify(authenticationRequest.getEmail())){
            authService.authenticate(authenticationRequest.getEmail(), authenticationRequest.getPassword());
            final UserDetails userDetails = userDetailsService
                    .loadUserByUsername(authenticationRequest.getEmail());

            final String token = jwtTokenUtil.generateToken(userDetails);

            return ResponseEntity.ok(new JwtResponse(token));
        }
        return new ResponseEntity<>("Not confirm",HttpStatus.OK);
    }

    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public ResponseEntity<AgentDTO> createUser(@RequestBody AgentDTO agentDTO) throws MessagingException {
        return new ResponseEntity<>(authService.createAgent(agentDTO),HttpStatus.OK);
    }

    @PostMapping("/verify")
    public ResponseEntity<?> verifyUser(@RequestParam String email, @RequestParam int confirmationNumber) {
        return new ResponseEntity<>(authService.verifyUser(email,confirmationNumber),HttpStatus.OK);
    }

    @PostMapping("/forgetPassword")
    public ResponseEntity<String> forgetPassword(@RequestParam String email){
        return new ResponseEntity<>(authService.forgetPsw(email), HttpStatus.OK);
    }

    @PostMapping("/forgetPassword/verify")
    public ResponseEntity<String> verifyForgetPsw(@RequestParam String email,
                                                  @RequestParam int confirmationNumber,
                                                  @RequestParam String newPassword){
        return new ResponseEntity<>(authService.verifyForgetPsw(email,confirmationNumber,newPassword), HttpStatus.OK);
    }

    @PostMapping("/changePsw")
    public ResponseEntity<String> changePassword(HttpServletRequest request,
                                                 @RequestParam String oldPsw,
                                                 @RequestParam  String newPsw){
        System.out.println("enetred");
        Agent agent = jwtTokenUtil.getUserId(request.getHeader("Authorization"));
        return new ResponseEntity<>(authService.changePassword(agent,oldPsw,newPsw), HttpStatus.OK);
    }

    @GetMapping("/time")
    public ResponseEntity<?> getTime(){
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
