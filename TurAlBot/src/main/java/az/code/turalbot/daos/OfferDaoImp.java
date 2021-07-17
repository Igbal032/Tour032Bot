package az.code.turalbot.daos;

import az.code.turalbot.dtos.OfferDTO;
import az.code.turalbot.models.Offer;
import az.code.turalbot.repos.OfferRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class OfferDaoImp implements OfferDAO{

    private final OfferRepo offerRepo;
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
}
