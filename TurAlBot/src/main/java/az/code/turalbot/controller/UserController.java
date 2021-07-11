package az.code.turalbot.controller;

import az.code.turalbot.models.Question;
import az.code.turalbot.services.TurAlBotService;

import lombok.RequiredArgsConstructor;

import org.glassfish.grizzly.utils.Pair;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class UserController {
    private final TurAlBotService turAlBotService;

    @GetMapping("/info")
    public ResponseEntity<List<Pair<String, String>>> getAnswers(@RequestParam long chatId) {
        System.out.println("Excellent!! "+ chatId);
        List<Pair<String, String>> pairs = new ArrayList<>();
        turAlBotService.questionsAndAnswers(chatId).entrySet().forEach(w->
                        System.out.println("Sual: "+w.getKey()+" - "+w.getValue())
                );
        return new ResponseEntity<>(HttpStatus.OK);
    }
    @GetMapping("/info")
    public ResponseEntity<List<Pair<String, String>>> sendMessage(@RequestParam long chatId) {
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
