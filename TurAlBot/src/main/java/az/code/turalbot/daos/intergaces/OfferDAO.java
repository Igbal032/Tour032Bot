package az.code.turalbot.daos.intergaces;

import az.code.turalbot.dtos.OfferDTO;
import az.code.turalbot.models.Agent;
import az.code.turalbot.models.ConfirmOffer;
import az.code.turalbot.models.Offer;

import java.util.List;

public interface OfferDAO{
    Offer save(Offer offer);
    Offer hasOffer(Agent agent, String UUID);
    List<Offer> getOffersWithUuidAnIsShow(String UUID,boolean isShow);
    void setIsShowOnOffer(Offer offer,Integer msjId);
    boolean isExistOffer(OfferDTO offerDTO);
    Offer getOffersWithUuidAnMsjId(String UUID, Integer msjId);
    List<Offer> offers(Agent agent);
}
