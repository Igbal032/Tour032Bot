package az.code.turalbot.services;

import az.code.turalbot.cache.SessionCash;
import az.code.turalbot.models.Action;
import az.code.turalbot.models.Language;
import az.code.turalbot.cache.Session;
import az.code.turalbot.utils.GenerateUUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class SessionServiceImp implements SessionService{

    private final SessionCash sessionCash;

    public Session saveSession(long chatId, Action action, Boolean isProgress,
                               Language language, Map<String,String> answers,
                               boolean isNew){
        Session session = Session.builder()
                .UUID(GenerateUUID.generateUUID())
                .chatId(chatId)
                .currentAction(action)
                .questionsAndAnswers(answers)
                .isProgress(isProgress)
                .currentLanguage(language)
                .build();
        System.out.println(session.getUUID()+" START UUID");
        return sessionCash.save(session);
    }

    @Override
    public Session findByChatId(Long chatId) {
        return sessionCash.findByChatId(chatId);
    }

    @Override
    public void delete(Session session) {
        sessionCash.delete(session);
    }

    @Override
    public void updateSession(Long chatId,Action action, Boolean isProgress,
                              Language language, Map<String,String> answers) {
        Session update = findByChatId(chatId);
        update.setCurrentAction(action);
        update.setCurrentLanguage(language);
        update.setIsProgress(isProgress);
        update.setQuestionsAndAnswers(answers);
       sessionCash.save(update);
    }
}
