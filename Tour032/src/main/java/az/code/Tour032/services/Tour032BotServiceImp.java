package az.code.Tour032.services;

import az.code.Tour032.Tour032TelegramBot;
import az.code.Tour032.cache.Cache;
import az.code.Tour032.cache.ImageCache;
import az.code.Tour032.cache.Session;
import az.code.Tour032.daos.intergaces.RequestDAO;
import az.code.Tour032.models.*;
import az.code.Tour032.models.Button;
import az.code.Tour032.repos.*;
import az.code.Tour032.services.interfaces.OfferService;
import az.code.Tour032.services.interfaces.RequestService;
import az.code.Tour032.services.interfaces.SessionService;
import az.code.Tour032.services.interfaces.Tour032BotService;
import az.code.Tour032.utils.Utils;
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
import java.util.List;

@Service
@RequiredArgsConstructor
public class Tour032BotServiceImp implements Tour032BotService {

    private final LanguageRepo languageRepo;
    private final ActionsRepo actionsRepo;
    private final TranslateRepo translateRepo;
    private final ButtonsRepo buttonsRepo;
    private final NotificationRepo notificationRepo;
    private final RequestDAO requestDAO;
    private final RequestService requestService;
    private final Tour032TelegramBot tour032TelegramBot;
    private final SessionService sessionService;
    private final OfferService offerService;
    private final Cache imageCache;

    @Override
    public  SendMessage handlerInputMessage(Message message){

        SendMessage sendMessage = new SendMessage();
        if (message.getText().equals("/start")){
            return startChat(message.getChatId());
        }
        if (message.getText().equals("/stop")){
            return stopChat(message.getChatId());
        }
        Session session = sessionService.findByChatId(message.getChatId());
        if (session==null){
            return returnNotification(message.getChatId(),"finish");
        }
        if (session.getCurrentAction().getNextId()==null){
            System.out.println(message.getText()+" in handlerInputMessage");
            return handleNotifications(session, message);
        }
        if (!correctAnswerOrNot(session.getCurrentAction().getQuestion().getId(),session.getChatId(),message.getText())
                &&session.getCurrentAction().getType().equals("button")){
            return returnNotification(message.getChatId(),"wrongAnswer");
        }
        if (session.getCurrentAction().getQuestion().getRegex()!=null){
            if (!Utils.regexForData(message.getText().trim(),session.getCurrentAction().getQuestion().getRegex())){
                return returnNotification(message.getChatId(),session.getCurrentAction().getQuestion().getTypeOfNotification());
            }
        }
        addAnswerToCache(session,message);
        sendMessage = getQuestion(session.getCurrentAction().getNextId(),
                session.getCurrentLanguage(),session.getChatId());
        sendMessage.setChatId(session.getChatId());
        return sendMessage;
    }

    @Override
    public SendMessage stopChat(Long chatId) {
        Session session  = sessionService.findByChatId(chatId);
        try{
            if (session!=null){
                if (session.getCurrentAction().getNextId()!=null){
                    SendMessage sendMessage = returnNotification(chatId,"stop");
                    sessionService.delete(session);
                    return sendMessage;
                }
                requestService.sendStopRequestToStopRabbitMQ(session.getUUID());
                SendMessage sendMessage = returnNotification(chatId,"stop");
                sessionService.delete(session);
                imageCache.delete(imageCache.findByUUID(session.getUUID()));
                return sendMessage;
            }
            return returnNotification(chatId,"finish");
        }
        catch (Exception ex){
            System.out.println("error during stop");
            sessionService.delete(session);
            imageCache.delete(imageCache.findByUUID(session.getUUID()));
            return new SendMessage();
        }
    }

    public SendMessage handleNotifications(Session session, Message message){
        if (imageCache.findByUUID(session.getUUID()).getCountOfSendingImage()!=0){
            if(message.getReplyToMessage()!=null){
                String result = offerService
                        .acceptOffer(message.getReplyToMessage().getMessageId(),session.getUUID(),message.getText());
                if (result.equals("wrongPhoneNumber")){
                    return returnNotification(message.getChatId(),"wrongPhoneNumber");
                }
                else if(result.equals("noMessageId")){
                    return returnNotification(message.getChatId(),"correctReply");
                }
                return returnNotification(message.getChatId(),"willBeCalled");
            }
            return returnNotification(message.getChatId(),"selectOffer");
        }
        return returnNotification(message.getChatId(),"wait");
    }

    public void addAnswerToCache(Session session,Message message){
        session.getQuestionsAndAnswers()
                .put(session.getCurrentAction().getQuestion().getKeyWord(),message.getText());
        sessionService.updateSession(message.getChatId(),session.getCurrentAction()
                ,session.getIsProgress(),session.getCurrentLanguage()
                ,session.getQuestionsAndAnswers());
    }


    @Override
    public SendMessage startChat(Long chatId) {
        Session session = sessionService.findByChatId(chatId);
        if (session!=null){
            return returnNotification(chatId,"progress");
        }
        Action currentState = actionsRepo.findActionWithQId(1l);
        Language language  = languageRepo.getById(1l);
        sessionService.saveSession(chatId,currentState,true, language, new HashMap<>(),true);
        SendMessage sendMessage = new SendMessage();
        sendMessage = getQuestion(1l,language,chatId);
        sendMessage.setChatId(chatId);
        return sendMessage;
    }

    @Override
    public SendMessage returnNotification(long chatId, String type){
        Session session = sessionService.findByChatId(chatId);
        Language language;
        long langId;
        if (session==null||(session!=null&&session.getCurrentLanguage()==null)){
            langId = 1l;
        }else {
            language = session.getCurrentLanguage();
            langId = language.getId();
            System.out.println(language.getLanguageName()+ "  notification");
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
        Session session = sessionService.findByChatId(callbackQuery.getMessage().getChatId());
        if (session==null){
            SendMessage notification = returnNotification(callbackQuery.getMessage().getChatId(),"finish");
            return answerCallBackQuery(notification.getText(),true,callbackQuery.getId());
        }
        if (callbackQuery.getData().equals("more")){
            return tour032TelegramBot.sendMoreOffer(session);
        }
        boolean isCorrectAnswer = correctAnswerOrNot(session.getCurrentAction().getQuestion().getId(),
                callbackQuery.getMessage().getChatId(), callbackQuery.getData());
        if (!isCorrectAnswer){
            SendMessage notification = returnNotification(callbackQuery.getMessage().getChatId(),"wrongAnswer");
            return answerCallBackQuery(notification.getText(), true, callbackQuery.getId());
        }
        SendMessage callBackAnswer = null;
        long qId = session.getCurrentAction().getNextId();
        if (session.getCurrentAction().getQuestion().getId()==1){
            Language language = languageRepo.getById(Long.parseLong(callbackQuery.getData()));
            System.out.println(language.getLanguageName());
            session.setCurrentLanguage(language);
            sessionService.updateSession(callbackQuery.getMessage().getChatId()
                    ,session.getCurrentAction(),session.getIsProgress()
                    ,session.getCurrentLanguage(),session.getQuestionsAndAnswers());
        }
        if (callbackQuery.getData().equals("isPass")){
            Action action  = actionsRepo.findActionWithQId(qId);
            callBackAnswer = getQuestion(action.getNextId()
                    ,session.getCurrentLanguage(),callbackQuery.getMessage().getChatId());
            callBackAnswer.setChatId(callbackQuery.getMessage().getChatId());
            saveAnswerToCash(callbackQuery.getMessage().getChatId(),
                    session.getCurrentAction().getQuestion().getKeyWord(),
                    callbackQuery.getData());
            return callBackAnswer;
        }
        callBackAnswer = getQuestion(qId
                ,session.getCurrentLanguage(),
                callbackQuery.getMessage().getChatId());
        callBackAnswer.setChatId(session.getChatId());
        saveAnswerToCash(callbackQuery.getMessage().getChatId(),
                session.getCurrentAction().getQuestion().getKeyWord(),
                callbackQuery.getData());
        return callBackAnswer;
    }

    @Override
    public void saveAnswerToCash(long chatId, String keyWord, String callBackData){
        Session session = sessionService.findByChatId(chatId);
        Button findButton = buttonsRepo.getButtonWithCallBackAndLangId(callBackData
                ,1l);
        if (keyWord!=null){
            session.getQuestionsAndAnswers().put(keyWord,findButton.getKeyWord());
            sessionService.updateSession(chatId,session.getCurrentAction()
                    ,session.getIsProgress(),session.getCurrentLanguage()
                    ,session.getQuestionsAndAnswers());
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
        Session session = sessionService.findByChatId(chatId);
        Language lang = session.getCurrentLanguage();
        List<Button> buttonList = buttonsRepo.getButtons(questionId,lang.getId());
        Optional<Button> findButton = buttonList.stream()
                .filter(button -> button.getButtonCallBack()
                        .equals(data))
                .findAny();
        return findButton.isPresent();
    }

    @Override
    public AnswerCallbackQuery answerCallBackQuery(String message, boolean isShow,
                                                   String callBackQueryId) {
        AnswerCallbackQuery answerCallbackQuery = new AnswerCallbackQuery();
        answerCallbackQuery.setCallbackQueryId(callBackQueryId);
        answerCallbackQuery.setShowAlert(isShow);
        answerCallbackQuery.setText(message);
        return answerCallbackQuery;
    }

    @Override
    public SendMessage getQuestion(long questionId, Language language, Long chatId) {
        Action action = actionsRepo.findActionWithQId(questionId);
        SendMessage sendMessage = new SendMessage();
        if (action.getType().equals("button")){
            List<Button> buttonList=null;
            if (questionId==1){
                sendMessage.setText(action.getQuestion().getContent());
            }else{
                Translate translate = translateRepo.getTranslate(language.getId(),action.getQuestion().getId());
                sendMessage.setText(translate.getTranslatedContent());
            }
            buttonList = buttonsRepo.getButtons(questionId,language.getId());
            sendMessage.setReplyMarkup(createButtons(buttonList));
        }else if (action.getType().equals("freetext")){
            Translate translate =  translateRepo.getTranslate(language.getId(), questionId);
            sendMessage.setText(translate.getTranslatedContent());
        }
        if(action.getNextId()==null){
            saveRequest(chatId);
        }
        Session session = sessionService.findByChatId(chatId);
        session.setCurrentAction(action);
        sessionService.updateSession(session.getChatId(),
                session.getCurrentAction(),session.getIsProgress(),
                session.getCurrentLanguage(),session.getQuestionsAndAnswers());
        return sendMessage;
    }

    public void saveRequest(Long chatId){
        Session session = sessionService.findByChatId(chatId);
        StringBuffer jsonText = new StringBuffer();
        jsonText.append("{");
        session.getQuestionsAndAnswers().entrySet().forEach(w->{
            jsonText.append('"'+w.getKey()+'"'+':'+'"'+w.getValue()+'"').append(',');
        });
        jsonText.append("}").deleteCharAt(jsonText.lastIndexOf(","));
        requestService.sendRequestToRabbitMQ(chatId, jsonText.toString(),
                session.getUUID());
        addImageCache(chatId,session.getUUID());
    }

    public void addImageCache(Long chatId, String UUID){
        imageCache.save(ImageCache.builder()
                .chatId(chatId)
                .UUID(UUID)
                .countOfAllImages(0)
                .isAccess(true)
                .build()
        );
    }

}
