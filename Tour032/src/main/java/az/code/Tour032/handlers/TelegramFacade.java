package az.code.Tour032.handlers;

//import az.code.turalbot.cache.UserDataCache;
import az.code.Tour032.services.interfaces.Tour032BotService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
@Component
@RequiredArgsConstructor
public class TelegramFacade {


    private final Tour032BotService tour032BotService;

    public BotApiMethod<?> handlerUpdate(Update update){
        SendMessage reply  = null;
        if (update.hasCallbackQuery()){
            CallbackQuery callbackQuery = update.getCallbackQuery();
            return tour032BotService.processCallBack(callbackQuery);
        }
        Message message = update.getMessage();
        if (message!=null&&message.hasText()){
            reply = tour032BotService.handlerInputMessage(message);
        }
        return reply;
    }
}
