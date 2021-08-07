package az.code.Tour032.config;

import az.code.Tour032.Tour032TelegramBot;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.meta.ApiContext;

@Setter
@Getter
@Configuration
@ConfigurationProperties(prefix = "telegrambot")
public class BotConfig {

    private String userName;
    private String botToken;
    private String webHookPath;

    @Bean
    public Tour032TelegramBot TurAlTelegramBotBean(){
        DefaultBotOptions defaultBotOptions  = ApiContext.getInstance(DefaultBotOptions.class);
        Tour032TelegramBot tour032TelegramBot = new Tour032TelegramBot(defaultBotOptions);
        tour032TelegramBot.setWebHookPath(webHookPath);
        tour032TelegramBot.setUserName(userName);
        tour032TelegramBot.setBotToken(botToken);
        return tour032TelegramBot;
    }


}
