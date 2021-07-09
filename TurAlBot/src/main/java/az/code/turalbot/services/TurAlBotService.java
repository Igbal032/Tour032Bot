package az.code.turalbot.services;

import az.code.turalbot.models.Button;
import az.code.turalbot.models.Language;
import az.code.turalbot.models.Question;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.List;
import java.util.Map;

public interface TurAlBotService {
    InlineKeyboardMarkup createButtons(List<Button> buttonList);
    SendMessage getQuestion(long questionId, Language language, String UUID, Long chatId);
    SendMessage handlerInputMessage(Message message);
    BotApiMethod<?> processCallBack(CallbackQuery callbackQuery);
    Map<String, String> questionsAndAnswers(long chatId);
    InlineKeyboardButton createInlineKeyboardButton(Button button);
    boolean correctAnswerOrNot(long questionId, long chatId,String data);
    AnswerCallbackQuery answerCallBackQuery(String message,boolean isShow, String callBackQueryId);
    void saveData(long chatId, String keyWord, String callBackData);
}