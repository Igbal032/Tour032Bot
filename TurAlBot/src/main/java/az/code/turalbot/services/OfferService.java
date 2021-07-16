package az.code.turalbot.services;

import az.code.turalbot.dtos.OfferDTO;
import az.code.turalbot.models.Offer;

public interface OfferService {

    Offer createOffer(OfferDTO offerDTO, Integer messageId, boolean isShow);
}
