package az.code.turalbot.services;

import az.code.turalbot.daos.OfferDAO;
import az.code.turalbot.dtos.OfferDTO;
import az.code.turalbot.models.ConfirmOffer;
import az.code.turalbot.models.Offer;
import az.code.turalbot.repos.OfferRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class OfferServiceImp implements OfferService {
    private final OfferDAO offerDAO;
    private static final String PHONE_REGEX = "[+]{1}[9]{2}[4]{1}(([5]([0]|[1]|[5]))|([7]([0]|[7]))|([9]([9])))[1-9][0-9]{6}";
//    private final RabbitTemplate template;
//    private final TopicExchange exchange2;
//    @Value("${sample.rabbitmq.receiverKey}")
//    String receiverKey;
    @Override
    public Offer createOffer(OfferDTO offerDTO, Integer messageId, boolean isShow) {
        Offer offer = Offer.builder()
                .UUID(offerDTO.getUUID())
                .chatId(offerDTO.getChatId())
                .companyName("Company Name")
                .file(offerDTO.getFile())
                .imgPath(offerDTO.getUUID())
                .isShow(isShow)
                .messageId(messageId)
                .build();
        return offerDAO.createOffer(offer);
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
