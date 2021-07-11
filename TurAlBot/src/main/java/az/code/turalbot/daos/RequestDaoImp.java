package az.code.turalbot.daos;

import az.code.turalbot.models.Requests;
import az.code.turalbot.repos.RequestRepo;
import az.code.turalbot.utils.GenerateUUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class RequestDaoImp implements RequestDAO{

    private final RequestRepo requestRepo;

    @Override
    public Requests getRequestByIsActiveAndChatId(Long chatId, boolean isActive) {
        Requests findRequest = requestRepo.getRequestByIsActiveAndChatId(chatId,true);
        findRequest.setActive(false);
        requestRepo.save(findRequest);
        return findRequest;
    }

    @Override
    public Requests saveRequest(Long chatId, String jsonText) {
        Requests newRequests = Requests.builder()
                .UUID(GenerateUUID.generateUUID())
                .chatId(chatId)
                .isActive(true)
                .jsonText(jsonText)
                .createdDate(LocalDateTime.now())
                .build();
        Requests requests =  requestRepo.save(newRequests);
        return requests;
    }
}
