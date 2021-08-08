package az.code.Tour032.services;

import az.code.Tour032.Exceptions.RequestNotFoundException;
import az.code.Tour032.daos.intergaces.OfferDAO;
import az.code.Tour032.dtos.ImageDTO;
import az.code.Tour032.dtos.OfferDTO;
import az.code.Tour032.dtos.ReplyMessageDTO;
import az.code.Tour032.enums.OfferStatus;
import az.code.Tour032.enums.RequestStatus;
import az.code.Tour032.models.*;
import az.code.Tour032.repos.AgentRepo;
import az.code.Tour032.repos.RequestRepo;
import az.code.Tour032.repos.RequestToAgentRepo;
import az.code.Tour032.services.interfaces.OfferService;
import az.code.Tour032.utils.Utils;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class OfferServiceImp implements OfferService {
    private final OfferDAO offerDAO;
    private final RequestToAgentRepo requestToAgentRepo;
    private final RequestRepo requestRepo;
    private final AgentRepo agentRepo;
    private static final String PHONE_REGEX = "[+]{1}[9]{2}[4]{1}(([5]([0]|[1]|[5]))|([7]([0]|[7]))|([9]([9])))[1-9][0-9]{6}";
    private final RabbitTemplate template;
    private final TopicExchange exchange;
    @Value("${sample.rabbitmq.offerKey}")
    String offerKey;
    @Value("${sample.rabbitmq.acceptKey}")
    String acceptKey;

    @Override
    public String sendOfferToRabBitMQ(String UUID, ImageDTO imageDTO, Agent agent) throws IOException {
        Requests requests = requestRepo.getRequestsByUUID(UUID);
        if (requests==null){
            throw new RequestNotFoundException("Request not found!!");
        }
        if (!requests.isActive()){
            return "Request is deActive";
        }
        if (requests.getRequestStatus().equals(RequestStatus.EXPIRED.toString())){
            return "Request is EXPIRED";
        }
        if (!isOfferSend(UUID,agent)){
            Path imgPath = Paths.get(Utils.generateImageBasedOnText(imageDTO,agent.getCompanyName()));
            byte[] fileContent = Files.readAllBytes(imgPath);
            Files.delete(imgPath);
            OfferDTO offer = OfferDTO.builder()
                    .UUID(UUID)
                    .agentId(agent.getId())
                    .chatId(requests.getChatId())
                    .file(fileContent)
                    .build();
            template.convertAndSend(exchange.getName(),offerKey,offer);
            return "Offer was sent";
        }
        return "You have already sent offer";
    }

    @Override
    public Offer sendReplyRequestToRabBitMQ(String UUID, Integer msjId, String phoneNumber) {
        Offer offer = offerDAO.getOffersWithUuidAnMsjId(UUID,msjId);
        if (offer!=null) {
            ReplyMessageDTO dto = ReplyMessageDTO.builder()
                    .UUID(UUID)
                    .phoneNumber(phoneNumber)
                    .messageId(msjId)
                    .build();
            template.convertAndSend(exchange.getName(),acceptKey,dto);
            System.out.println("SENT REPLY REQUEST TO RABBIT MQ");
            return offer;
        }
        return null;
    }

    @Override
    @RabbitListener(queues = "acceptQueue")
    public void listenReplyRequestFromRabBitMQ(ReplyMessageDTO dto) {
        Offer findOffer = offerDAO.getOffersWithUuidAnMsjId(dto.getUUID(), dto.getMessageId());
        findOffer.setPhoneNumber(dto.getPhoneNumber());
        findOffer.setOfferStatus(OfferStatus.ACCEPT.toString());
        offerDAO.save(findOffer);
        System.out.println("SAVE REPLY REQUEST FROM RABBIT MQ");
    }

    @Override
    public List<Offer> offers(Agent agent) {
        return offerDAO.offers(agent);
    }

    @Override
    public Offer createOffer(OfferDTO offerDTO, Integer messageId, boolean isShow) {
        Agent agent = agentRepo.getById(offerDTO.getAgentId());
        Requests requests = requestRepo.getRequestsByUUID(offerDTO.getUUID());
        Offer newOffer = Offer.builder()
                    .UUID(offerDTO.getUUID())
                    .chatId(offerDTO.getChatId())
                    .agent(agent)
                    .offerStatus(OfferStatus.OFFER_SENT.toString())
                    .file(offerDTO.getFile())
                    .imgPath(offerDTO.getUUID())
                    .isShow(isShow)
                    .messageId(messageId)
                    .build();
        RequestToAgent requestToAgent = requestToAgentRepo.getRequestToAgentByAgentAndRequests(agent,requests);
        changeStatusOfRequestToAgent(requestToAgent, OfferStatus.OFFER_SENT);
        return offerDAO.save(newOffer);
    }

    public void changeStatusOfRequestToAgent(RequestToAgent requestToAgent, OfferStatus requestStatus){
        requestToAgent.setRequestStatus(requestStatus.toString());
        requestToAgentRepo.save(requestToAgent);
    }

    public boolean isOfferSend(String UUID, Agent agent){
        Offer offer = offerDAO.hasOffer(agent,UUID);
        if(offer==null){
            return false;
        }
        return true;
    }

    @Override
    public List<Offer> getOffersWithUuidAnIsShow(String UUID, boolean isShow){
        return offerDAO.getOffersWithUuidAnIsShow(UUID,isShow);
    }

    @Override
    public void setIsShowOnOffer(Offer offer,Integer msjId){
         offerDAO.setIsShowOnOffer(offer, msjId);
    }

    @Override
    public boolean isExistOffer(OfferDTO offerDTO) {
        return offerDAO.isExistOffer(offerDTO);
    }

    @Override
    public String acceptOffer(Integer msjId, String UUID, String phoneNumber) {
        if (checkPhoneNumber(phoneNumber)){
            Offer acceptOffer = sendReplyRequestToRabBitMQ(UUID,msjId,phoneNumber);
            if (acceptOffer==null){
                return "noMessageId";
            }
            return "success";
        }
        return "wrongPhoneNumber";
    }

    public boolean checkPhoneNumber(String phone){
        Pattern pattern = Pattern.compile(PHONE_REGEX);
        Matcher matcher = pattern.matcher(phone);
        return matcher.matches();
    }
}
