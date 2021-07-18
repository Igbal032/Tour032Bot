package az.code.turalbot.daos;

import az.code.turalbot.dtos.OfferDTO;
import az.code.turalbot.models.ConfirmOffer;
import az.code.turalbot.models.Offer;
import az.code.turalbot.repos.ConfirmOfferRepo;
import az.code.turalbot.repos.OfferRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class OfferDaoImp implements OfferDAO{

    private final OfferRepo offerRepo;
    private final ConfirmOfferRepo confirmOfferRepo;

    @Override
    public Offer createOffer(Offer offer) {
        return offerRepo.save(offer);
    }

    @Override
    public List<Offer> getOffersWithUuidAnIsShow(String UUID, boolean isShow) {
        return offerRepo.getOffersWithUuidAnIsShow(UUID,isShow);
    }

    @Override
    public void setIsShowOnOffer(Offer offer,Integer msjId) {
            offer.setShow(true);
            offer.setMessageId(msjId);
            offerRepo.save(offer);
    }

    @Override
    public boolean isExistOffer(OfferDTO offerDTO) {
        List<Offer> offerList = offerRepo.getOffersWithUuidAnIsShow(offerDTO.getUUID(),false);
        if (offerList.size()==0){
            return false;
        }
        return true;
    }
    @Override
    public ConfirmOffer sendConfirmToRabbitMQ(Integer msjId, String UUID,String phoneNumber){ //todo
        Offer offer = offerRepo.getOfferByUUIDAndMessageId(UUID,msjId);
        if (offer!=null) {
            ConfirmOffer confirmOffer = ConfirmOffer.builder()
                    .chatId(offer.getChatId())
                    .companyName(offer.getCompanyName())
                    .UUID(offer.getUUID())
                    .file(offer.getFile())
                    .phoneOrUserName(phoneNumber)
                    .build();
            return confirmOfferRepo.save(confirmOffer);
        }
        return null;
    }
    @Override
    public Offer getOffersWithUuidAnMsjId(String UUID, Integer msjId) {
        return offerRepo.getOfferByUUIDAndMessageId(UUID, msjId);
    }
//    @Override
//    public ConfirmOffer createConfirmOffer(Integer msjId, String UUID,String phoneOrUserName) {
//        Offer offer = offerRepo.getOfferByUUIDAndMessageId(UUID,msjId);
//        if (offer!=null){
//            ConfirmOffer confirmOffer = ConfirmOffer.builder()
//                    .chatId(offer.getChatId())
//                    .companyName(offer.getCompanyName())
//                    .UUID(offer.getUUID())
//                    .file(offer.getFile())
//                    .phoneOrUserName(phoneOrUserName)
//                    .build();
//            confirmOfferRepo.save(confirmOffer);
//            return confirmOffer;
//        }
//        return null;
//    }
}
