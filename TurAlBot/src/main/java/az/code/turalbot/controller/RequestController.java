package az.code.turalbot.controller;

import az.code.turalbot.TurAlTelegramBot;
import az.code.turalbot.cache.Cache;
import az.code.turalbot.config.JwtTokenUtil;
import az.code.turalbot.dtos.ImageDTO;
import az.code.turalbot.models.Agent;
import az.code.turalbot.models.Requests;
import az.code.turalbot.repos.AgentRepo;
import az.code.turalbot.services.interfaces.OfferService;
import az.code.turalbot.services.interfaces.RequestService;
import az.code.turalbot.services.interfaces.TurAlBotService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@RestController
@RequestMapping("/api/request")
@RequiredArgsConstructor
public class RequestController {
    private final TurAlBotService turAlBotService;
    private final RequestService requestService;
    private final OfferService offerService;
    private final AgentRepo agentRepo;
    private final JwtTokenUtil jwtTokenUtil;
    private final Cache cache;
    private final TurAlTelegramBot turAlTelegramBot;
    @GetMapping("/info")
    public ResponseEntity<String> getAnswers(@RequestParam String uuid) {
        Requests requests = requestService.getRequestWithUUID(uuid);
        return new ResponseEntity<>(requests.getJsonText(),HttpStatus.OK);
    }
    @PostMapping("/clients")
    public ResponseEntity<String> sendImage(@RequestBody ImageDTO imageDTO,
                                            HttpServletRequest request) throws IOException {
        Agent agent = jwtTokenUtil.getUserId(request.getHeader("Authorization"));
        System.out.println(agent.getName());
        String answer = offerService.sendOfferToRabBitMQ(imageDTO.getUUID(),imageDTO,agent);
        return new ResponseEntity<>(answer,HttpStatus.OK);
    }
}
