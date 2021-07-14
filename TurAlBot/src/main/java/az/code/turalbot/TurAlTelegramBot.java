package az.code.turalbot;

import az.code.turalbot.handlers.TelegramFacade;
import az.code.turalbot.services.TurAlBotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.bots.TelegramWebhookBot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updates.SetWebhook;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.*;
public class TurAlTelegramBot extends TelegramWebhookBot {

    private String userName;
    private String botToken;
    private String webHookPath;
    @Autowired
    private TurAlBotService turAlBotService;
    @Autowired
    private TelegramFacade telegramFacade;

    public TurAlTelegramBot(TurAlBotService turAlBotService) {
        this.turAlBotService = turAlBotService;
    }

    public TurAlTelegramBot(DefaultBotOptions defaultBotOptions) {
        super(defaultBotOptions);
    }

    @Override
    public BotApiMethod onWebhookUpdateReceived(Update update) {
        final BotApiMethod<?>  replyBot =  telegramFacade.handlerUpdate(update);
//        SetWebhook setWebhook =
        return replyBot;
    }

    @Override
    public String getBotUsername() {
        return userName;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }

    @Override
    public String getBotPath() {
        return null;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setBotToken(String botToken) {
        this.botToken = botToken;
    }

    public void setWebHookPath(String webHookPath) {
        this.webHookPath = webHookPath;
    }
}