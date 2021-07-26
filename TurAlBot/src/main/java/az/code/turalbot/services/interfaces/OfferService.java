package az.code.turalbot.services.interfaces;

import az.code.turalbot.dtos.ImageDTO;
import az.code.turalbot.dtos.OfferDTO;
import az.code.turalbot.dtos.ReplyMessageDTO;
import az.code.turalbot.models.Agent;
import az.code.turalbot.models.ConfirmOffer;
import az.code.turalbot.models.Offer;

import java.io.IOException;
import java.util.List;

public interface OfferService {

    Offer createOffer(OfferDTO offerDTO, Integer messageId, boolean isShow);
    List<Offer> getOffersWithUuidAnIsShow(String UUID, boolean isShow);
    void setIsShowOnOffer(Offer offerList, Integer msjId);
    boolean isExistOffer(OfferDTO offerDTO);
    String acceptOffer(Integer msjId, String UUID, String phoneOrUserName);
    String sendOfferToRabBitMQ(String UUID, ImageDTO imageDTO, Agent agent) throws IOException;
    Offer sendReplyRequestToRabBitMQ(String UUID, Integer msjId, String phoneNumber);
    void listenReplyRequestFromRabBitMQ(ReplyMessageDTO dto);
    List<Offer> offers(Agent agent);
}
