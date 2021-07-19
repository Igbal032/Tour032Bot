package az.code.turalbot.repos;

import az.code.turalbot.models.Offer;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.AssertionsForClassTypes.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@SpringBootTest
class OfferRepoTest {

    @Autowired
    private OfferRepo testOfferRepo;

    @Test
    void GetOffersWithUuidAnIsShow() {
        //given
        List<Offer> createList = IntStream.range(0,8).mapToObj(w->{
            return  Offer.builder().UUID("ef36af087d7543f3b50f90caed752ab1")
                    .chatId(12345l+w).companyName("TestCompany")
                    .file(null).messageId(5858).isShow(true).build();
        }).collect(Collectors.toList());
        testOfferRepo.saveAll(createList);
        //when
        List<Offer> expectedOfferList = testOfferRepo
                .getOffersWithUuidAnIsShow("ef36af087d7543f3b50f90caed752ab1"
                        ,true);
        //then
        assertThat(expectedOfferList.size()).isEqualTo(5);
    }

    @Test
    void getOfferByUUIDAndMessageId() {
        //givem
        Integer randomMessageId = new Random().nextInt(45555);
        Offer createdOffer = Offer.builder().UUID("ef36af087d7543f3b50f90caed752ab1")
                .chatId(123455l).companyName("TestCompany32")
                .file(null).messageId(randomMessageId).isShow(true).build();
        testOfferRepo.save(createdOffer);
        //when
        Offer expectedOffer = testOfferRepo.getOfferByUUIDAndMessageId("ef36af087d7543f3b50f90caed752ab1",randomMessageId);
        //then
        assertThat(createdOffer.getCompanyName()).isEqualTo(expectedOffer.getCompanyName());
    }
}