package az.code.turalbot.controller;

import az.code.turalbot.config.JwtTokenUtil;
import az.code.turalbot.dtos.OfferDTO;
import az.code.turalbot.models.Agent;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.PUT;

@RestController
@RequestMapping("/api/offer")
@RequiredArgsConstructor
public class OfferController {

    private final JwtTokenUtil jwtTokenUtil;


}
