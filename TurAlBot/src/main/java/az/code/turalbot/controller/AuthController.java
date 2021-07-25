package az.code.turalbot.controller;

import az.code.turalbot.config.JwtTokenUtil;
import az.code.turalbot.dtos.AgentDTO;
import az.code.turalbot.models.Auth.JwtRequest;
import az.code.turalbot.models.Auth.JwtResponse;
import az.code.turalbot.services.JwtUserDetailsService;
import az.code.turalbot.services.interfaces.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private JwtUserDetailsService userDetailsService;

    @Autowired
    private AuthService authService;



    @RequestMapping(value = "/authenticate", method = RequestMethod.POST)
    public ResponseEntity<?> createAuthenticationToken(@RequestBody JwtRequest authenticationRequest) throws Exception {
        if (authService.isVerify(authenticationRequest.getEmail())){
            authenticate(authenticationRequest.getEmail(), authenticationRequest.getPassword());

            final UserDetails userDetails = userDetailsService
                    .loadUserByUsername(authenticationRequest.getEmail());

            final String token = jwtTokenUtil.generateToken(userDetails);

            return ResponseEntity.ok(new JwtResponse(token));
        }
        return new ResponseEntity<>("Not confirm",HttpStatus.OK);
    }

    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public ResponseEntity<AgentDTO> createUser(@RequestBody AgentDTO agentDTO) {
        return new ResponseEntity<>(authService.createAgent(agentDTO),HttpStatus.OK);
    }

    @GetMapping("/verify/{test}")
    public ResponseEntity<?> verifyUser(@PathVariable String test) {
        System.out.println(test+" success");
        System.out.println("Verified controller");
        return null;
//        return new ResponseEntity<>(authService.checkToken(email,token),HttpStatus.OK);
    }

    private void authenticate(String username, String password) throws Exception {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        } catch (DisabledException e) {
            throw new Exception("USER_DISABLED", e);
        } catch (BadCredentialsException e) {
            throw new Exception("INVALID_CREDENTIALS", e);
        }
    }
}
