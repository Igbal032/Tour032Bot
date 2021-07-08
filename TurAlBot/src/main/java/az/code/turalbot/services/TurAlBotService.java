package az.code.turalbot.services;

import az.code.turalbot.models.Button;
import az.code.turalbot.models.Language;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

import java.util.List;

public interface TurAlBotService {
    InlineKeyboardMarkup createButtons(List<Button> buttonList);
    List<Language> getLanguages();
    SendMessage getQuestion(long questionId, Language language, String UUID, Long chatId);
    SendMessage handlerInputMessage(Message message);
    BotApiMethod<?> processCallBack(CallbackQuery callbackQuery);
}
