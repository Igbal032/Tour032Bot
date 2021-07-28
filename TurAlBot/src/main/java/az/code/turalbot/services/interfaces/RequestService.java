package az.code.turalbot.services.interfaces;

import az.code.turalbot.dtos.ImageDTO;
import az.code.turalbot.models.Agent;
import az.code.turalbot.models.RequestToAgent;
import az.code.turalbot.models.Requests;
import org.springframework.web.multipart.MultipartFile;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;

import javax.ws.rs.core.Response.*;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

public interface RequestService {

    Requests getRequestWithUUID(String UUID);

    void sendRequestToRabbitMQ(Long chatId, String jsonText,String UUID);

    void listenRequestFromRabbitMQ(Requests request);

    void sendStopRequestToStopRabbitMQ(String UUID);

    void listenStopRequestFromRabbitMQ(String UUID);

    RequestToAgent addArchive(long agentId, long requestId);

    List<Requests> getRequestsBasedOnAgentAndStatus(Agent agent, String status);

    LocalDateTime calculateDeadline();

    void checkExpiredDate();

}
