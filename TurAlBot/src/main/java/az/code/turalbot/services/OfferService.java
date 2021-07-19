package az.code.turalbot.services;

import az.code.turalbot.dtos.OfferDTO;
import az.code.turalbot.models.ConfirmOffer;
import az.code.turalbot.models.Offer;

import java.util.List;

public interface OfferService {

    Offer createOffer(OfferDTO offerDTO, Integer messageId, boolean isShow);
    List<Offer> getOffersWithUuidAnIsShow(String UUID, boolean isShow);
    void setIsShowOnOffer(Offer offerList, Integer msjId);
    boolean isExistOffer(OfferDTO offerDTO);
    String createConfirmOffer(Integer msjId, String UUID,String phoneOrUserName);
}
