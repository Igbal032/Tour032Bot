package az.code.turalbot.services;

import az.code.turalbot.Exceptions.RequestNotFoundException;
import az.code.turalbot.daos.intergaces.OfferDAO;
import az.code.turalbot.daos.intergaces.RequestDAO;
import az.code.turalbot.daos.intergaces.RequestToAgentDAO;
import az.code.turalbot.dtos.ImageDTO;
import az.code.turalbot.dtos.OfferDTO;
import az.code.turalbot.enums.RequestStatus;
import az.code.turalbot.models.Agent;
import az.code.turalbot.models.Offer;
import az.code.turalbot.models.RequestToAgent;
import az.code.turalbot.models.Requests;
import az.code.turalbot.services.interfaces.RequestService;
import az.code.turalbot.services.interfaces.TurAlBotService;
import az.code.turalbot.utils.Utils;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.annotation.RabbitListeners;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.ws.rs.core.Request;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RequestServiceImp implements RequestService {

    private final RequestDAO requestDAO;
    private final RequestToAgentDAO requestToAgentDAO;
    private final RabbitTemplate template;
    private final TopicExchange exchange;
    @Value("${sample.rabbitmq.requestKey}")
    String requestKey;
    @Value("${sample.rabbitmq.stopKey}")
    String stopKey;
    @Value("${company.start.time}")
    String startedDate;
    @Value("${company.end.time}")
    String endedDate;
    @Value("${company.work.hours}")
    String workHours;
    @Override
    public Requests getRequestWithUUID(String UUID) {
        return requestDAO.getWithUUID(UUID);
    }

    @Override
    public void sendRequestToRabbitMQ(Long chatId, String jsonText,String UUID) {
        Requests newRequests = Requests.builder()
                .UUID(UUID).chatId(chatId)
                .isActive(true).jsonText(jsonText)
                .requestStatus(RequestStatus.ACTIVE.toString())
                .build();
        template.convertAndSend(exchange.getName(),requestKey,newRequests);
        System.out.println("Sent to Request Queue");
    }

    @Override
    @RabbitListener(queues = "requestQueue")
    public void listenRequestFromRabbitMQ(Requests request) {
        request.setCreatedDate(LocalDateTime.now());
        request.setExpiredDate(calculateDeadline());
        Requests savedRequest = requestDAO.save(request);
        requestToAgentDAO.saveRequestForPerAgent(savedRequest);
        System.out.println("Save to DB");
    }

    @Override
    public LocalDateTime calculateDeadline(){
        LocalTime startHour = LocalTime.parse(startedDate);
        LocalTime endHour = LocalTime.parse(endedDate);
        LocalTime workHour = LocalTime.parse(workHours);
        LocalDateTime current = LocalDateTime.now();
        LocalDateTime end = LocalDateTime.of(LocalDate.now(),endHour);
        LocalDateTime start = LocalDateTime.of(LocalDate.now(),startHour);
        if (end.isAfter(current)&&start.isBefore(current)){
            LocalTime remainForToday = differenceCurrentTimeAndEnd(current,end);
            LocalDateTime remainForNextDay = LocalDateTime.of(LocalDate.now(),remainForToday);
            LocalTime remainHours = differenceRemainTimeAndWorkHour(remainForNextDay,workHour);
            LocalDateTime expiredDate = LocalDateTime.of(LocalDate.now().plusDays(1),startHour
                    .plusHours(remainHours.getHour())
                    .plusMinutes(remainHours.getMinute())
                    .plusSeconds(remainHours.getSecond()));
            System.out.println(expiredDate);
            return expiredDate;
        }
        else {
            if (LocalTime.now().isBefore(startHour)){
                LocalDateTime expiredDate = LocalDateTime.of(LocalDate.now(),endHour);
                return expiredDate;
            }
            else {
                LocalDateTime expiredDate = LocalDateTime.of(LocalDate.now().plusDays(1),endHour);
                return expiredDate;
            }
        }
    }

    @Override
    @Scheduled(fixedRate = 5000)
    public void checkExpiredDate() {
       List<Requests> requestsList = requestDAO.getRequestsWithStatus(RequestStatus.ACTIVE.toString());
       requestsList.forEach(r->{
           if (LocalDateTime.now().isAfter(r.getExpiredDate())){
               r.setRequestStatus(RequestStatus.EXPIRED.toString());
               requestDAO.save(r);
               List<RequestToAgent> requestToAgentList = requestToAgentDAO.getRequestToAgentByReqId(r.getId());
               requestToAgentList.forEach(rr->{
                   rr.setRequestStatus(RequestStatus.EXPIRED.toString());
                   requestToAgentDAO.save(rr);
               });
           }
       });
    }

    public LocalTime differenceCurrentTimeAndEnd(LocalDateTime current, LocalDateTime end){
        LocalDateTime tempDateTime = LocalDateTime.from(current);
        int hours = (int)tempDateTime.until( end, ChronoUnit.HOURS );
        tempDateTime = tempDateTime.plusHours( hours );
        int minutes = (int)tempDateTime.until( end, ChronoUnit.MINUTES );
        tempDateTime = tempDateTime.plusMinutes( minutes );
        int second = (int)tempDateTime.until( end, ChronoUnit.SECONDS );
        return LocalTime.of(hours,minutes,second);
    }

    public LocalTime differenceRemainTimeAndWorkHour(LocalDateTime remainForNextDay, LocalTime workHour){
        LocalTime tempDateTime2 = LocalTime.from(workHour);
        int hours = (int)tempDateTime2.until( remainForNextDay, ChronoUnit.HOURS );
        tempDateTime2 = tempDateTime2.plusHours( hours );
        int minutess = (int)tempDateTime2.until( remainForNextDay, ChronoUnit.MINUTES );
        tempDateTime2 = tempDateTime2.plusMinutes( minutess );
        int seconss = (int)tempDateTime2.until( remainForNextDay, ChronoUnit.SECONDS );
        return LocalTime.of(Math.abs(hours),Math.abs(minutess),Math.abs(seconss));
    }

    @Override
    public void sendStopRequestToStopRabbitMQ(String UUID) {
        template.convertAndSend(exchange.getName(),stopKey,UUID);
        System.out.println("Send STOP to RABBIT MQ");
    }

    @Override
    @RabbitListener(queues = "stopQueue")
    public void listenStopRequestFromRabbitMQ(String UUID) {
        requestDAO.deactivateStatus(UUID);
        System.out.println("SAVE STOP FROM RABBIT MQ");
    }

    @Override
    public RequestToAgent addArchive(long agentId, long requestId) {
        RequestToAgent requestToAgent = requestToAgentDAO.getRequestByAgentIdAndRequestId(agentId,requestId);
        if (requestToAgent!=null){
            requestToAgent.setArchive(true);
            return requestToAgentDAO.save(requestToAgent);
        }
        throw new RequestNotFoundException("Request Not Found!!");
    }

    @Override
    public List<Requests> getRequestsBasedOnAgentAndStatus(Agent agent, String status) {
        List<RequestToAgent> requestToAgentList = requestToAgentDAO.getRequestsBasedOnAgentAndStatus(agent,status);
        List<Requests> requests = requestToAgentList.stream().map(w->{
            return w.getRequests();
        }).collect(Collectors.toList());
        return requests;
    }

    @Override
    public List<Requests> getRequestsByArchive(Agent agent) {
        List<RequestToAgent> requestToAgentList = requestToAgentDAO.getRequestsOnArchive(agent);
        List<Requests> requests = requestToAgentList.stream().map(w->{
            return w.getRequests();
        }).collect(Collectors.toList());
        return requests;
    }
}
