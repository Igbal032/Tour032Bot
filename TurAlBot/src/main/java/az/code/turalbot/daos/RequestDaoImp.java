package az.code.turalbot.daos;

import az.code.turalbot.Exceptions.RequestNotFoundException;
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
    public Requests deactivateStatus(String UUID) {
        Requests findRequest = requestRepo.getRequestsByUUID(UUID);
        findRequest.setActive(false);
        requestRepo.save(findRequest);
        System.out.println("save" + findRequest.isActive());
        return findRequest;
    }

    @Override
    public Requests getWithUUID(String UUID) {
        Requests request   = requestRepo.getRequestsByUUID(UUID);
        if (request==null){
            throw new RequestNotFoundException("Request not found");
        }
        return request;
    }

    @Override
    public Requests saveRequest(Long chatId, String jsonText,String UUID) {
        Requests newRequests = Requests.builder()
                .UUID(UUID)
                .chatId(chatId)
                .isActive(true)
                .jsonText(jsonText)
                .createdDate(LocalDateTime.now())
                .build();
        Requests requests =  requestRepo.save(newRequests);
        return requests;
    }
}
