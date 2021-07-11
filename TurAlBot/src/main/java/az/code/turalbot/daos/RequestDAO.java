package az.code.turalbot.daos;

import az.code.turalbot.models.Requests;

public interface RequestDAO {
    Requests getRequestByIsActiveAndChatId(Long chatId,boolean isActive);

    Requests saveRequest(Long chatId, String jsonText);
}
