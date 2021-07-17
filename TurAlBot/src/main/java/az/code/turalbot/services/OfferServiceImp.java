package az.code.turalbot.services;

import az.code.turalbot.daos.OfferDAO;
import az.code.turalbot.dtos.OfferDTO;
import az.code.turalbot.models.Offer;
import az.code.turalbot.repos.OfferRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

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


}
