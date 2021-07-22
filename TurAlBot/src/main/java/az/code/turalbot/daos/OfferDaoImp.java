package az.code.turalbot.daos;

import az.code.turalbot.daos.intergaces.OfferDAO;
import az.code.turalbot.dtos.OfferDTO;
import az.code.turalbot.enums.OfferStatus;
import az.code.turalbot.models.Agent;
import az.code.turalbot.models.ConfirmOffer;
import az.code.turalbot.models.Offer;
import az.code.turalbot.repos.ConfirmOfferRepo;
import az.code.turalbot.repos.OfferRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class OfferDaoImp implements OfferDAO {

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
        List<Offer> offerList = offerRepo.getOffersWithUuidAnIsShow(offerDTO.getUUID()
                ,false);
        if (offerList.size()==0){
            return false;
        }
        return true;
    }

    @Override
    public ConfirmOffer sendConfirmToRabbitMQ(Integer msjId, String UUID,String phoneNumber){ //todo add to RABBITMQ
        Offer offer = offerRepo.getOfferByUUIDAndMessageId(UUID,msjId);
        if (offer!=null) {
            ConfirmOffer confirmOffer = ConfirmOffer.builder()
                    .chatId(offer.getChatId())
                    .companyName(offer.getAgent().getCompanyName())
                    .UUID(offer.getUUID())
                    .file(offer.getFile())
                    .phoneNumber(phoneNumber)
                    .build();
            offer.setOfferStatus(OfferStatus.ACCEPT.toString());
            offerRepo.save(offer);
            return confirmOfferRepo.save(confirmOffer);
        }
        return null;
    }
    @Override
    public Offer getOffersWithUuidAnMsjId(String UUID, Integer msjId) {
        return offerRepo.getOfferByUUIDAndMessageId(UUID, msjId);
    }

    @Override
    public Offer hasOffer(Agent agent, String UUID){
        Offer offer = offerRepo.hasOffer(agent.getId(),UUID);
        return offer;
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
