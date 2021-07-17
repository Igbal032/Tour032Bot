package az.code.turalbot.repos;

import az.code.turalbot.models.Offer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface OfferRepo extends JpaRepository<Offer, Long> {
//    @Query("select o from Offer o where o.UUID=:UUID and o.isShow=:isShow order by o.createdDate asc")

    @Query(value = "SELECT * FROM offers o WHERE o.uuid=:UUID and o.is_show=:isShow ORDER BY o.created_date ASC LIMIT 5", nativeQuery = true)
    List<Offer> getOffersWithUuidAnIsShow(String UUID, boolean isShow);

}
