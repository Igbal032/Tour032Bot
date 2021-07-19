package az.code.turalbot.controller;

import az.code.turalbot.TurAlTelegramBot;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;

@RestController
public class BotController {

    private final TurAlTelegramBot turAlTelegramBot;
    public BotController(TurAlTelegramBot turAlTelegramBot) {
        this.turAlTelegramBot = turAlTelegramBot;
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handlerNotFoundException(Exception exception) {
        System.out.println(exception.getMessage());
        return new ResponseEntity<>(exception.getMessage(),HttpStatus.OK);
    }

    @RequestMapping(value = "/", method = RequestMethod.POST)
    public BotApiMethod<?> onUpdateReceive(@RequestBody Update update){
        return turAlTelegramBot.onWebhookUpdateReceived(update);
    }
}
