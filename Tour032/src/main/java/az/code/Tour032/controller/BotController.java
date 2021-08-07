package az.code.Tour032.controller;

import az.code.Tour032.Tour032TelegramBot;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;

@RestController
public class BotController {

    private final Tour032TelegramBot tour032TelegramBot;
    public BotController(Tour032TelegramBot tour032TelegramBot) {
        this.tour032TelegramBot = tour032TelegramBot;
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handlerNotFoundException(Exception exception) {
        System.out.println(exception.getMessage());
        return new ResponseEntity<>(exception.getMessage(),HttpStatus.OK);
    }

    @RequestMapping(value = "/", method = RequestMethod.POST)
    public BotApiMethod<?> onUpdateReceive(@RequestBody Update update){
        return tour032TelegramBot.onWebhookUpdateReceived(update);
    }
}
