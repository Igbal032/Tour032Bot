package az.code.turalbot.repos;

import az.code.turalbot.models.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface NotificationRepo extends JpaRepository<Notification, Long> {

    @Query("select n from Notification n where n.language.id=:lang and n.notificationType=:type")
    Notification getNotificationByLangAndType(Long lang, String type);
}
