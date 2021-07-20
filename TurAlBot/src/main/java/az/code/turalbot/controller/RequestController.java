package az.code.turalbot.controller;

import az.code.turalbot.Exceptions.RequestNotFoundException;
import az.code.turalbot.TurAlTelegramBot;
import az.code.turalbot.cache.Cache;
import az.code.turalbot.models.Requests;
import az.code.turalbot.services.interfaces.RequestService;
import az.code.turalbot.services.interfaces.TurAlBotService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class RequestController {
    private final TurAlBotService turAlBotService;
    private final RequestService requestService;
    private final Cache cache;
    private final TurAlTelegramBot turAlTelegramBot;
    @GetMapping("/info")
    public ResponseEntity<String> getAnswers(@RequestParam String uuid) {
        Requests requests = requestService.getRequestWithUUID(uuid);
        return new ResponseEntity<>(requests.getJsonText(),HttpStatus.OK);
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
