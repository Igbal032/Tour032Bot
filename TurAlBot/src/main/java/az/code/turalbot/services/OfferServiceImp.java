package az.code.turalbot.services;

import az.code.turalbot.daos.intergaces.OfferDAO;
import az.code.turalbot.dtos.ImageDTO;
import az.code.turalbot.dtos.OfferDTO;
import az.code.turalbot.enums.OfferStatus;
import az.code.turalbot.enums.RequestStatus;
import az.code.turalbot.models.*;
import az.code.turalbot.repos.AgentRepo;
import az.code.turalbot.repos.RequestRepo;
import az.code.turalbot.repos.RequestToAgentRepo;
import az.code.turalbot.services.interfaces.OfferService;
import az.code.turalbot.utils.Utils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
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
//    private final RabbitTemplate template;
//    private final TopicExchange exchange2;
//    @Value("${sample.rabbitmq.receiverKey}")
//    String receiverKey;
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
        return offerDAO.createOffer(newOffer);
    }

    public void changeStatusOfRequestToAgent(RequestToAgent requestToAgent, OfferStatus requestStatus){
        requestToAgent.setRequestStatus(requestStatus.toString());
        requestToAgentRepo.save(requestToAgent);
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
    public String createConfirmOffer(Integer msjId, String UUID,String phoneNumber) {
        if (checkPhoneNumber(phoneNumber)){
            ConfirmOffer confirmOffer = offerDAO.sendConfirmToRabbitMQ(msjId,UUID,phoneNumber);
            if (confirmOffer==null){
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
