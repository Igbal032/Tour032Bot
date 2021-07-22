package az.code.turalbot.repos;

import az.code.turalbot.models.Agent;
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
    private AgentRepo agentRepo;

    @Test
    void GetOffersWithUuidAnIsShow() {
        //given
        Agent agent = Agent.builder()
                .companyName("Test")
                .email("sasas")
                .name("Intiqam")
                .password("123456")
                .build();
        List<Offer> createList = IntStream.range(0,8).mapToObj(w->{
            return  Offer.builder().UUID("ef36af087d7543f3b50f90caed752ab1")
                    .chatId(12345l+w).agent(agent)
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
        //given
        Agent agent = Agent.builder()
                .companyName("Test")
                .email("sasas")
                .name("Intiqam")
                .password("123456")
                .build();
        Integer randomMessageId = new Random().nextInt(45555);
        Offer createdOffer = Offer.builder().UUID("ef36af087d7543f3b50f90caed752ab1")
                .chatId(123455l).agent(agent)
                .file(null).messageId(randomMessageId).isShow(true).build();
        testOfferRepo.save(createdOffer);
        //when
        Offer expectedOffer = testOfferRepo.getOfferByUUIDAndMessageId("ef36af087d7543f3b50f90caed752ab1",randomMessageId);
        //then
        assertThat(createdOffer.getAgent().getCompanyName()).isEqualTo(expectedOffer.getAgent().getCompanyName());
    }
}