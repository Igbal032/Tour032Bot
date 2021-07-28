package az.code.turalbot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;

@EnableScheduling
@SpringBootApplication
public class TurAlBotApplication {

    public static void main(String[] args) {
        SpringApplication.run(TurAlBotApplication.class, args);
    }

}
