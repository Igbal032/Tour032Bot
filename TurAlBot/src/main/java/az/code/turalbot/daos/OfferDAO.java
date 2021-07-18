package az.code.turalbot.daos;

import az.code.turalbot.dtos.OfferDTO;
import az.code.turalbot.models.ConfirmOffer;
import az.code.turalbot.models.Offer;

import java.util.List;

public interface OfferDAO{
    Offer createOffer(Offer offer);
    List<Offer> getOffersWithUuidAnIsShow(String UUID,boolean isShow);
    void setIsShowOnOffer(Offer offer,Integer msjId);
    boolean isExistOffer(OfferDTO offerDTO);
    Offer getOffersWithUuidAnMsjId(String UUID, Integer msjId);
    ConfirmOffer sendConfirmToRabbitMQ(Integer msjId, String UUID,String phoneNumber);
}
