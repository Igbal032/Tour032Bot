package az.code.turalbot.repos;

import az.code.turalbot.models.Language;
import az.code.turalbot.models.Notification;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.AssertionsForClassTypes.*;

@SpringBootTest
class NotificationRepoTest {
    @Autowired
    private LanguageRepo languageRepo;
    @Autowired
    private NotificationRepo notificationRepo;
    @Test
    void getNotificationByLangAndType() {
        //given
        Language language = Language.builder().languageName("AZERBAYCAN DILI")
                .build();
        languageRepo.save(language);
        Notification notification = Notification.builder()
                .language(language).notificationType("serverError").notificationText("Server closed")
                .build();
        notificationRepo.save(notification);
        //when
        Notification expected = notificationRepo.getNotificationByLangAndType(language.getId(),
                "serverError");
        //then
        assertThat(notification.getNotificationType())
                .isEqualTo(expected.getNotificationType());
        assertThat(notification.getLanguage().getLanguageName())
                .isEqualTo(expected.getLanguage().getLanguageName());
        assertThat(notification.getNotificationText())
                .isEqualTo(expected.getNotificationText());
    }
}