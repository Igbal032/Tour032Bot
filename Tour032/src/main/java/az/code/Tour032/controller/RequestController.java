package az.code.Tour032.controller;

import az.code.Tour032.config.JwtTokenUtil;
import az.code.Tour032.dtos.ImageDTO;
import az.code.Tour032.models.Agent;
import az.code.Tour032.models.Offer;
import az.code.Tour032.models.Requests;
import az.code.Tour032.services.interfaces.OfferService;
import az.code.Tour032.services.interfaces.RequestService;

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


    @GetMapping("/")
    public ResponseEntity<?> activeRequests(){
        return new ResponseEntity<>(requestService.getAllRequests(),HttpStatus.OK);
    }

    @PostMapping("/sendOffer")
    public ResponseEntity<String> sendOffer(@RequestBody ImageDTO imageDTO,
                                            HttpServletRequest request) throws IOException {
        Agent agent = jwtTokenUtil.getUserId(request.getHeader("Authorization"));
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
        return new ResponseEntity<>(requestService.getRequestsByArchive(agent),HttpStatus.OK);
    }

    @GetMapping("/offers")
    public ResponseEntity<List<Offer>> offers(HttpServletRequest request){
        Agent agent = jwtTokenUtil.getUserId(request.getHeader("Authorization"));
        return new ResponseEntity<>(offerService.offers(agent),HttpStatus.OK);
    }
}
