package az.code.turalbot.services;

import az.code.turalbot.cache.UserDataCache;
import az.code.turalbot.models.*;
import az.code.turalbot.repos.*;
import az.code.turalbot.utils.GenerateUUID;
import lombok.RequiredArgsConstructor;
import org.apache.commons.codec.language.bm.Lang;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.*;

@Service
@RequiredArgsConstructor
public class TurAlBotServiceImp implements TurAlBotService{

    private final LanguageRepo languageRepo;
    private final ActionsRepo actionsRepo;
    private final TranslateRepo translateRepo;
    private final ButtonsRepo buttonsRepo;
    private final NotificationRepo notificationRepo;
    Map<Long, Action> currentAction = new HashMap<>();
    Map<Long, Language> currentLanguage = new HashMap<>();
    Map<Long, Map<Question, String>> questionsAndAnswers = new HashMap<>();

    @Override
    public  SendMessage handlerInputMessage(Message message){
        SendMessage sendMessage = null;
        if (message.getText().equals("/start")){
            Action currentState = actionsRepo.findActionWithQId(1l);
            Language language  = languageRepo.getById(1l);
            currentAction.put(message.getChatId(),currentState);
            currentLanguage.put(message.getChatId(),language);
            sendMessage = getQuestion(1l,language,null,message.getChatId());
            sendMessage.setChatId(message.getChatId());
            return sendMessage;
        }
        Action botState = currentAction.get(message.getChatId());
        if (botState.getNextId()==null){
            sendMessage = getQuestion(currentAction.get(message.getChatId()).getQuestion().getId(),currentLanguage.get(message.getChatId()),null,message.getChatId());
            sendMessage.setChatId(message.getChatId());
            return sendMessage;
        }
        questionsAndAnswers.get(message.getChatId()).put(botState.getQuestion(),message.getText());
        sendMessage = getQuestion(currentAction.get(message.getChatId()).getNextId(),currentLanguage.get(message.getChatId()),null, message.getChatId());
        sendMessage.setChatId(message.getChatId());
        return sendMessage;
    }

    @Override
    public BotApiMethod<?> processCallBack(CallbackQuery callbackQuery){
        Action botState = currentAction.get(callbackQuery.getMessage().getChatId());
        boolean isCorrectAnswer = correctAnswerOrNot(botState.getQuestion().getId(),callbackQuery);
        if (!isCorrectAnswer){
            Notification notification = notificationRepo
                    .getNotificationByLangAndType(currentLanguage.get(callbackQuery.getMessage().getChatId())
                            .getId(),"wrongAnswer");
            return answerCallBackQuery(notification.getNotificationText(), true, callbackQuery.getId());
        }
        Map<Question, String> questionStringMap = new HashMap<>();
        SendMessage callBackAnswer = null;
        long qId = botState.getNextId();
        if (botState.getQuestion().getId()==1){
            Language language = languageRepo.getById(Long.parseLong(callbackQuery.getData()));
            currentLanguage.put(callbackQuery.getMessage().getChatId(),language);
            questionsAndAnswers.put(callbackQuery.getMessage().getChatId(),questionStringMap);
        }
        if (callbackQuery.getData().equals("isPass")){
            Action action  = actionsRepo.findActionWithQId(qId);
            callBackAnswer = getQuestion(action.getNextId()
                    ,currentLanguage.get(callbackQuery.getMessage().getChatId()),null,callbackQuery.getMessage().getChatId());
            callBackAnswer.setChatId(callbackQuery.getMessage().getChatId());
            questionsAndAnswers.get(callbackQuery.getMessage().getChatId()).put(botState.getQuestion(),
                    callbackQuery.getData());
            return callBackAnswer;
        }
        callBackAnswer = getQuestion(qId
                ,currentLanguage.get(callbackQuery.getMessage().getChatId()),null,callbackQuery.getMessage().getChatId());
        callBackAnswer.setChatId(callbackQuery.getMessage().getChatId());
        questionsAndAnswers.get(callbackQuery.getMessage().getChatId()).put(botState.getQuestion(), callbackQuery.getData());
        return callBackAnswer;
    }

    @Override
    public Map<Question, String> questionsAndAnswers(long chatId) {
        return questionsAndAnswers.get(chatId);
    }

    @Override
    public InlineKeyboardMarkup createButtons(List<Button> buttonList) {
        List<List<InlineKeyboardButton>> lists = new ArrayList<>();
        List<InlineKeyboardButton> inlineKeyboardButtons = new ArrayList();
        for (Button button: buttonList){
            inlineKeyboardButtons.add(createInlineKeyboardButton(button));
        }
        lists.add(inlineKeyboardButtons);
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        inlineKeyboardMarkup.setKeyboard(lists);
        return inlineKeyboardMarkup;
    }

    @Override
    public InlineKeyboardButton createInlineKeyboardButton(Button button) {
        InlineKeyboardButton inlineKeyboardButton = new InlineKeyboardButton();
        inlineKeyboardButton.setText(button.getButtonText());
        inlineKeyboardButton.setCallbackData(button.getButtonCallBack());
        return inlineKeyboardButton;
    }

    @Override
    public boolean correctAnswerOrNot(long questionId, CallbackQuery callbackQuery) {
        Language lang = currentLanguage.get(callbackQuery.getMessage().getChatId());
        List<Button> buttonList = buttonsRepo.getButtons(questionId,lang.getId());
        Optional<Button> findButton = buttonList.stream()
                .filter(button -> button.getButtonCallBack()
                        .equals(callbackQuery.getData()))
                .findAny();
        if (findButton.isPresent()){
            return true;
        }
        System.out.println("wrong answer");
        return false;
    }

    @Override
    public AnswerCallbackQuery answerCallBackQuery(String message, boolean isShow, String callBackQueryId) {
        AnswerCallbackQuery answerCallbackQuery = new AnswerCallbackQuery();
        answerCallbackQuery.setCallbackQueryId(callBackQueryId);
        answerCallbackQuery.setShowAlert(isShow);
        answerCallbackQuery.setText(message);
        return answerCallbackQuery;
    }

    @Override
    public boolean hasNextQuestion(Long chatId) {
        Action action = currentAction.get(chatId);
        if (action.getNextId()==null)
            return false;
        return true;
    }

    @Override
    public SendMessage getQuestion(long questionId, Language language,String UUID, Long chatId) {
        Action action = actionsRepo.findActionWithQId(questionId);
        SendMessage sendMessage = new SendMessage();
        if (action.getType().equals("button")){
            List<Button> buttonList=null;
            if (questionId==1){
                sendMessage.setText(action.getQuestion().getContent());
            }
            else{
                Translate translate = translateRepo.getTranslate(language.getId(),action.getQuestion().getId());
                sendMessage.setText(translate.getTranslatedContent());
            }
            buttonList = buttonsRepo.getButtons(questionId,language.getId());
            sendMessage.setReplyMarkup(createButtons(buttonList));
        }
        else if (action.getType().equals("freetext")){
            Translate translate =  translateRepo.getTranslate(language.getId(), questionId);
            sendMessage.setText(translate.getTranslatedContent());
        }
        else if (action.getType().equals("end")){
            Translate translate =  translateRepo.getTranslate(language.getId(), questionId);
            sendMessage.setText(translate.getTranslatedContent());
        }
        currentAction.put(chatId,action);
        return sendMessage;
    }


}
