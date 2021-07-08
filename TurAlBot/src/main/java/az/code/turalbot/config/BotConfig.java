package az.code.turalbot.config;

import az.code.turalbot.TurAlTelegramBot;
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
    public TurAlTelegramBot TurAlTelegramBotBean(){
        DefaultBotOptions defaultBotOptions  = ApiContext.getInstance(DefaultBotOptions.class);
        TurAlTelegramBot turAlTelegramBot = new TurAlTelegramBot(defaultBotOptions);
        turAlTelegramBot.setWebHookPath(webHookPath);
        turAlTelegramBot.setUserName(userName);
        turAlTelegramBot.setBotToken(botToken);
        return turAlTelegramBot;
    }


}
