package az.code.turalbot.controller;

import az.code.turalbot.TurAlTelegramBot;
import az.code.turalbot.cache.Cache;
import az.code.turalbot.config.JwtTokenUtil;
import az.code.turalbot.dtos.ImageDTO;
import az.code.turalbot.dtos.OfferDTO;
import az.code.turalbot.enums.RequestStatus;
import az.code.turalbot.models.Agent;
import az.code.turalbot.models.Offer;
import az.code.turalbot.models.RequestToAgent;
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
import java.util.List;

@RestController
@RequestMapping("/api/request")
@RequiredArgsConstructor
public class RequestController {
    private final RequestService requestService;

    private final OfferService offerService;
    private final JwtTokenUtil jwtTokenUtil;
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

    @PutMapping("/addArchive")
    public ResponseEntity<?> addArchive(HttpServletRequest request, @RequestParam long requestId){
        Agent agent = jwtTokenUtil.getUserId(request.getHeader("Authorization"));
        requestService.addArchive(agent.getId(),requestId);
        return new ResponseEntity<>("success",HttpStatus.OK);
    }

    @GetMapping("/archives")
    public ResponseEntity<List<Requests>> archives(HttpServletRequest request){
        Agent agent = jwtTokenUtil.getUserId(request.getHeader("Authorization"));
        return new ResponseEntity<>(requestService.getRequestsBasedOnAgentAndStatus(agent, RequestStatus.ARCHIVE.toString()),HttpStatus.OK);
    }

    @GetMapping("/offers")
    public ResponseEntity<List<Offer>> offers(HttpServletRequest request){
        Agent agent = jwtTokenUtil.getUserId(request.getHeader("Authorization"));
        return new ResponseEntity<>(offerService.offers(agent),HttpStatus.OK);
    }
}
