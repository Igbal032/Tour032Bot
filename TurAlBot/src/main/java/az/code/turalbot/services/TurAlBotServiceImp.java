package az.code.turalbot.services;

import az.code.turalbot.cache.UserDataCache;
import az.code.turalbot.models.Action;
import az.code.turalbot.models.Button;
import az.code.turalbot.models.Language;
import az.code.turalbot.models.Translate;
import az.code.turalbot.repos.ActionsRepo;
import az.code.turalbot.repos.ButtonsRepo;
import az.code.turalbot.repos.LanguageRepo;
import az.code.turalbot.repos.TranslateRepo;
import az.code.turalbot.utils.GenerateUUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class TurAlBotServiceImp implements TurAlBotService{

    private final LanguageRepo languageRepo;
    private final ActionsRepo actionsRepo;
    private final TranslateRepo translateRepo;
    private final ButtonsRepo buttonsRepo;
    Map<Long, Action> currentAction = new HashMap<>();
    Map<Long, Language> currentLanguage = new HashMap<>();

    @Override
    public SendMessage handlerInputMessage(Message message){
        SendMessage sendMessage = null;
        if (message.getText().equals("/start")){
            Action currentState = actionsRepo.findActionWithQId(1l);
            System.out.println(currentState.getQuestion().getContent());
            Language language  = languageRepo.getById(1l);
            System.out.println(language.getLanguageName());
            currentAction.put(message.getChatId(),currentState);
            currentLanguage.put(message.getChatId(),language);
            sendMessage = getQuestion(1l,language,null,message.getChatId());
            sendMessage.setChatId(message.getChatId());
            return sendMessage;
        }
        sendMessage = getQuestion(currentAction.get(message.getChatId()).getNextId(),currentLanguage.get(message.getChatId()),null, message.getChatId());
        sendMessage.setChatId(message.getChatId());
        return sendMessage;
    }

    @Override
    public BotApiMethod<?> processCallBack(CallbackQuery callbackQuery){
        Action botState = currentAction.get(callbackQuery.getMessage().getChatId());
        if (botState.getQuestion().getId()==1){
            Language language = languageRepo.getById(Long.parseLong(callbackQuery.getData()));
            currentLanguage.put(callbackQuery.getMessage().getChatId(),language);
        }
        SendMessage callBackAnswer = getQuestion(botState.getNextId()
                ,currentLanguage.get(callbackQuery.getMessage().getChatId())
                ,null
                ,callbackQuery.getMessage().getChatId());
        callBackAnswer.setChatId(callbackQuery.getMessage().getChatId());
        return callBackAnswer;
    }


    @Override
    public InlineKeyboardMarkup createButtons(List<Button> buttonList) {
        List<List<InlineKeyboardButton>> lists = new ArrayList<>();
        List<InlineKeyboardButton> inlineKeyboardButtons = new ArrayList();
        for (Button button: buttonList){
            InlineKeyboardButton inlineKeyboardButton = new InlineKeyboardButton();
            inlineKeyboardButton.setText(button.getButtonText());
            inlineKeyboardButton.setCallbackData(button.getButtonCallBack());
            inlineKeyboardButtons.add(inlineKeyboardButton);
        }
        lists.add(inlineKeyboardButtons);
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        inlineKeyboardMarkup.setKeyboard(lists);
        return inlineKeyboardMarkup;
    }

    @Override
    public List<Language> getLanguages() {
        List<Language> allLanguage = languageRepo.findAll();
        return allLanguage;
    }

    @Override
    public SendMessage getQuestion(long questionId, Language language,String UUID, Long chatId) {
        Action action = actionsRepo.findActionWithQId(questionId);
        SendMessage sendMessage = new SendMessage();
        if (action.getType().equals("button")){
            List<Button> buttonList=null;
            if (questionId==1){
                buttonList = buttonsRepo.getLangButtons(questionId);
                sendMessage.setText(action.getQuestion().getContent());
            }
            else{
                Translate translate = translateRepo.getTranslate(language.getId(),action.getQuestion().getId());
                sendMessage.setText(translate.getTranslatedContent());
                buttonList = buttonsRepo.getButtons(questionId,language.getId());
            }
            sendMessage.setReplyMarkup(createButtons(buttonList));
        }
        else if (action.getType().equals("freetext")){
            Translate translate =  translateRepo.getTranslate(language.getId(), questionId);
            sendMessage.setText(translate.getTranslatedContent());
        }
        currentAction.put(chatId,action);
        return sendMessage;
    }



}
