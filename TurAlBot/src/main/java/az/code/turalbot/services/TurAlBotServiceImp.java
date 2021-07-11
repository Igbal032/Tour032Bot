package az.code.turalbot.services;

import az.code.turalbot.daos.RequestDAO;
import az.code.turalbot.models.*;
import az.code.turalbot.repos.*;
import az.code.turalbot.utils.GenerateUUID;
import az.code.turalbot.utils.Utils;
import lombok.RequiredArgsConstructor;
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
    private final RequestDAO requestDAO;
    Map<Long, Action> currentAction = new HashMap<>();
    Map<Long, Language> currentLanguage = new HashMap<>();
    Map<Long, Map<String, String>> questionsAndAnswers = new HashMap<>();
    Map<Long, Boolean> isProgress = new HashMap<>();

    @Override
    public  SendMessage handlerInputMessage(Message message){
        SendMessage sendMessage = new SendMessage();
        if (message.getText().equals("/start")){
            return startChat(message.getChatId());
        }
        if (message.getText().equals("/stop")){
            return stopChat(message.getChatId());
        }
        if (isProgress.get(message.getChatId())==null){
            System.out.println(message.getChatId());
            return returnNotification(message.getChatId(),"finish");
        }
        Action botState = currentAction.get(message.getChatId());
        if (!correctAnswerOrNot(botState.getQuestion().getId(),message.getChatId(),message.getText())
                &&botState.getType().equals("button")){
            return returnNotification(message.getChatId(),"wrongAnswer");
        }
        if (botState.getQuestion().getRegex()!=null){
            if (!Utils.regexForData(message.getText().trim(),botState.getQuestion().getRegex())){
                return returnNotification(message.getChatId(),botState.getQuestion().getTypeOfNotification());
            }
        }
        if (botState.getNextId()==null){
            return returnNotification(message.getChatId(),"wait");
        }
        questionsAndAnswers.get(message.getChatId()).
                put(botState.getQuestion().getKeyWord(),message.getText());
        sendMessage = getQuestion(currentAction.get(message.getChatId()).getNextId(),currentLanguage.get(message.getChatId()),null, message.getChatId());
        sendMessage.setChatId(message.getChatId());
        return sendMessage;
    }

    @Override
    public SendMessage stopChat(Long chatId) {
        Boolean isProgressOrNpt = isProgress.get(chatId);
        if (isProgressOrNpt!=null){
            isProgress.put(chatId,null);
            Requests findRequest = requestDAO.getRequestByIsActiveAndChatId(chatId,true);
            return returnNotification(chatId,"stop");
        }
        else {
            return returnNotification(chatId,"finish");
        }
    }

    @Override
    public SendMessage startChat(Long chatId) {
        if (isProgress.get(chatId)!=null){
            return returnNotification(chatId,"progress");
        }
        Action currentState = actionsRepo.findActionWithQId(1l);
        Language language  = languageRepo.getById(1l);
        currentAction.put(chatId,currentState);
        currentLanguage.put(chatId,language);
        isProgress.put(chatId,true);
        SendMessage sendMessage = new SendMessage();
        sendMessage = getQuestion(1l,language,null,chatId);
        sendMessage.setChatId(chatId);
        return sendMessage;
    }

    @Override
    public SendMessage returnNotification(long chatId, String type){
        Language language = currentLanguage.get(chatId);
        long langId;
        if (language==null){
            langId = 1l;
        }else {
            langId = language.getId();
        }
        Notification notification = notificationRepo
                    .getNotificationByLangAndType(langId,type);
        SendMessage sendMessage = new SendMessage();
        sendMessage.setText(notification.getNotificationText());
        sendMessage.setChatId(chatId);
        return sendMessage;
    }

    @Override
    public BotApiMethod<?> processCallBack(CallbackQuery callbackQuery){
        if (isProgress.get(callbackQuery.getMessage().getChatId())==null){
            SendMessage notification = returnNotification(callbackQuery.getMessage().getChatId(),"finish");
           return answerCallBackQuery(notification.getText(),true,callbackQuery.getId());
        }
        Action botState = currentAction.get(callbackQuery.getMessage().getChatId());
        boolean isCorrectAnswer = correctAnswerOrNot(botState.getQuestion().getId(),callbackQuery.getMessage().getChatId(), callbackQuery.getData());
        if (!isCorrectAnswer){
            SendMessage notification = returnNotification(callbackQuery.getMessage().getChatId(),"wrongAnswer");
            return answerCallBackQuery(notification.getText(), true, callbackQuery.getId());
        }
        Map<String, String> questionStringMap = new HashMap<>();
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

            saveData(callbackQuery.getMessage().getChatId(),botState.getQuestion().getKeyWord(),callbackQuery.getData());
            return callBackAnswer;
        }
        callBackAnswer = getQuestion(qId
                ,currentLanguage.get(callbackQuery.getMessage().getChatId()),null,callbackQuery.getMessage().getChatId());
        callBackAnswer.setChatId(callbackQuery.getMessage().getChatId());
        saveData(callbackQuery.getMessage().getChatId(),botState.getQuestion().getKeyWord(),callbackQuery.getData());
        return callBackAnswer;
    }

    @Override
    public Map<String, String> questionsAndAnswers(long chatId) {
        return questionsAndAnswers.get(chatId);
    }

    @Override
    public void saveData(long chatId, String keyWord, String callBackData){
        Button findButton = buttonsRepo.getButtonWithCallBackAndLangId(callBackData
                ,1l);
        if (keyWord!=null){
            questionsAndAnswers.get(chatId)
                    .put(keyWord,findButton.getKeyWord());
        }
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
    public boolean correctAnswerOrNot(long questionId, long chatId,String data) {
        Language lang = currentLanguage.get(chatId);
        List<Button> buttonList = buttonsRepo.getButtons(questionId,lang.getId());
        Optional<Button> findButton = buttonList.stream()
                .filter(button -> button.getButtonCallBack()
                        .equals(data))
                .findAny();
        return findButton.isPresent();
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
        if(action.getNextId()==null){
            saveRequest(chatId);
        }
        currentAction.put(chatId,action);
        return sendMessage;
    }

    public Requests saveRequest(Long chatId){
        StringBuffer jsonText = new StringBuffer();
        jsonText.append("{");
        questionsAndAnswers.get(chatId).entrySet().forEach(w->{
            jsonText.append('"'+w.getKey()+'"'+':'+'"'+w.getValue()+'"').append(',');
        });
        jsonText.append("}").deleteCharAt(jsonText.lastIndexOf(","));
        Requests requests = requestDAO.saveRequest(chatId, jsonText.toString());
        System.out.println(requests.getJsonText());
        return requests;
    }

}
