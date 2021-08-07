package az.code.Tour032.controller;

import az.code.Tour032.config.JwtTokenUtil;
import az.code.Tour032.dtos.AgentDTO;
import az.code.Tour032.models.Agent;
import az.code.Tour032.models.Auth.JwtRequest;
import az.code.Tour032.models.Auth.JwtResponse;
import az.code.Tour032.services.JwtUserDetailsService;
import az.code.Tour032.services.interfaces.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;

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
        Agent agent = jwtTokenUtil.getUserId(request.getHeader("Authorization"));
        return new ResponseEntity<>(authService.changePassword(agent,oldPsw,newPsw), HttpStatus.OK);
    }

    @GetMapping("/time/{param1}")
    public ResponseEntity<?> getTime(@PathVariable String param1){
        System.out.println("Ishledi"+ param1);
        return new ResponseEntity<>("Ishledi",HttpStatus.OK);
    }

}
