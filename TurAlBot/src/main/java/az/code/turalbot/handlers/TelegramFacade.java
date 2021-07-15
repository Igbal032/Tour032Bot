package az.code.turalbot.handlers;

//import az.code.turalbot.cache.UserDataCache;
import az.code.turalbot.models.Action;
import az.code.turalbot.repos.ActionsRepo;
import az.code.turalbot.services.TurAlBotService;
import az.code.turalbot.utils.GenerateUUID;
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


    private final TurAlBotService turAlBotService;

    public BotApiMethod<?> handlerUpdate(Update update){
        SendMessage reply  = null;
        if (update.hasCallbackQuery()){
            CallbackQuery callbackQuery = update.getCallbackQuery();
            return turAlBotService.processCallBack(callbackQuery);
        }
        Message message = update.getMessage();
        if (message!=null&&message.hasText()){
            reply = turAlBotService.handlerInputMessage(message);
        }
        return reply;
    }
}
