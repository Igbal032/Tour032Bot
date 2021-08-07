package az.code.Tour032.services.interfaces;

import az.code.Tour032.models.Button;
import az.code.Tour032.models.Language;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.List;

public interface Tour032BotService {
    InlineKeyboardMarkup createButtons(List<Button> buttonList);
    SendMessage getQuestion(long questionId, Language language, Long chatId);
    SendMessage handlerInputMessage(Message message);
    SendMessage stopChat(Long chatId);
    SendMessage startChat(Long chatId);
    SendMessage returnNotification(long chatId, String type);
    BotApiMethod<?> processCallBack(CallbackQuery callbackQuery);
    InlineKeyboardButton createInlineKeyboardButton(Button button);
    boolean correctAnswerOrNot(long questionId, long chatId,String data);
    AnswerCallbackQuery answerCallBackQuery(String message,boolean isShow, String callBackQueryId);
    void saveAnswerToCash(long chatId, String keyWord, String callBackData);
}
