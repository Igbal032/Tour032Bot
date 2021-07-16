package az.code.turalbot.services;

import az.code.turalbot.daos.OfferDAO;
import az.code.turalbot.dtos.OfferDTO;
import az.code.turalbot.models.Offer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OfferServiceImp implements OfferService {
    private final OfferDAO offerDAO;

    @Override
    public Offer createOffer(OfferDTO offerDTO, Integer messageId, boolean isShow) {
        Offer offer = Offer.builder()
                .UUID(offerDTO.getUUID())
                .chatId(offerDTO.getChatId())
                .companyName(offerDTO.getCompanyName())
                .file(offerDTO.getFile())
                .imgPath(offerDTO.getUUID())
                .isShow(isShow)
                .messageId(messageId)
                .build();
        return offerDAO.createOffer(offer);
    }
}
