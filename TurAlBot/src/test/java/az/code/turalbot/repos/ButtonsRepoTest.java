package az.code.turalbot.repos;

import az.code.turalbot.models.Button;
import az.code.turalbot.models.Language;
import az.code.turalbot.models.Question;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import static org.assertj.core.api.AssertionsForClassTypes.*;

@SpringBootTest
class ButtonsRepoTest {

    @Autowired
    private LanguageRepo languageRepo;
    @Autowired
    private ButtonsRepo buttonsRepo;

    @Test
    void getButtonWithCallBackAndLangId() {
        //given
        Language language = Language.builder().languageName("AZERBAYCAN DILI")
                .build();
        languageRepo.save(language);
        Button button1 = new Button();
            button1.setButtonText("Test button");
            button1.setLanguage(language);
            button1.setButtonCallBack("callBack32");
        Button as =  buttonsRepo.save(button1);
        //when
        Button expected = buttonsRepo.getButtonWithCallBackAndLangId("callBack32",language.getId());
        //then
        assertThat(button1.getButtonCallBack())
                .isEqualTo(expected.getButtonCallBack());
        assertThat(button1.getButtonText())
                .isEqualTo(expected.getButtonText());

    }
}