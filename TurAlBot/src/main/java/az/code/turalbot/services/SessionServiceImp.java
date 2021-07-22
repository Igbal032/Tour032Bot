package az.code.turalbot.services;

import az.code.turalbot.cache.SessionCache;
import az.code.turalbot.models.Action;
import az.code.turalbot.models.Language;
import az.code.turalbot.cache.Session;
import az.code.turalbot.services.interfaces.SessionService;
import az.code.turalbot.utils.GenerateUUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class SessionServiceImp implements SessionService {

    private final SessionCache sessionCache;

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
        return sessionCache.save(session);
    }

    @Override
    public Session findByChatId(Long chatId) {
        return sessionCache.findByChatId(chatId);
    }

    @Override
    public void delete(Session session) {
        sessionCache.delete(session);
    }

    @Override
    public void updateSession(Long chatId,Action action, Boolean isProgress,
                              Language language, Map<String,String> answers) {
        Session update = findByChatId(chatId);
        update.setCurrentAction(action);
        update.setCurrentLanguage(language);
        update.setIsProgress(isProgress);
        update.setQuestionsAndAnswers(answers);
       sessionCache.save(update);
    }
}
