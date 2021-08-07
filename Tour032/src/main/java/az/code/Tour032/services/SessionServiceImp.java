package az.code.Tour032.services;

import az.code.Tour032.cache.SessionCache;
import az.code.Tour032.models.Action;
import az.code.Tour032.models.Language;
import az.code.Tour032.cache.Session;
import az.code.Tour032.services.interfaces.SessionService;
import az.code.Tour032.utils.GenerateUUID;
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
