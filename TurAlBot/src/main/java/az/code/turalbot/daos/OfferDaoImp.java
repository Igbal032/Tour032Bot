package az.code.turalbot.daos;

import az.code.turalbot.models.Offer;
import az.code.turalbot.repos.OfferRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OfferDaoImp implements OfferDAO{

    private final OfferRepo offerRepo;
    @Override
    public Offer createOffer(Offer offer) {
        return offerRepo.save(offer);
    }
}
