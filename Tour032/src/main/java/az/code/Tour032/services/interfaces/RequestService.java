package az.code.Tour032.services.interfaces;

import az.code.Tour032.models.Agent;
import az.code.Tour032.models.RequestToAgent;
import az.code.Tour032.models.Requests;

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

    List<Requests> getRequestsByArchive(Agent agent);

    List<Requests> getAllRequests();

    LocalDateTime calculateDeadline();

    void checkExpiredDate();

}
