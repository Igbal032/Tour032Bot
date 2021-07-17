package az.code.turalbot.controller;

import az.code.turalbot.Exceptions.RequestNotFoundException;
import az.code.turalbot.TurAlTelegramBot;
import az.code.turalbot.cache.Cache;
import az.code.turalbot.cache.ImageCache;
import az.code.turalbot.cache.Session;
import az.code.turalbot.models.Question;
import az.code.turalbot.models.Requests;
import az.code.turalbot.services.RequestService;
import az.code.turalbot.services.SessionService;
import az.code.turalbot.services.TurAlBotService;

import lombok.RequiredArgsConstructor;

import org.glassfish.grizzly.utils.Pair;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class UserController {
    private final TurAlBotService turAlBotService;
    private final RequestService requestService;
    private final Cache cache;
    private final TurAlTelegramBot turAlTelegramBot;
    @GetMapping("/info")
    public ResponseEntity<String> getAnswers(@RequestParam String uuid) {
        Requests requests = requestService.getRequestWithUUID(uuid);
        return new ResponseEntity<>(requests.getJsonText(),HttpStatus.OK);
    }
    @PostMapping("/access")
    public ResponseEntity<String> access(@RequestParam String uuid) {
        turAlTelegramBot.removeMessage(6187,1054087888l,new Session());
        return new ResponseEntity<>("OK",HttpStatus.OK);
    }
    @PostMapping("/clients")
    public ResponseEntity<String> sendImage(@RequestParam String uuid, @RequestParam("file") MultipartFile file) throws IOException {
        Requests requests = requestService.getRequestWithUUID(uuid);
        if (requests==null){
            throw new RequestNotFoundException("Request not found!!");
        }
        if (requests.isActive()){
            requestService.sendDataToRabBitMQ(uuid,file);
            return new ResponseEntity<>("Image was send to queue",HttpStatus.OK);
        }
        else {
            return new ResponseEntity<>("Request is deActive",HttpStatus.OK);
        }
    }
}
