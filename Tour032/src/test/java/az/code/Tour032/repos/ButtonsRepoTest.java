package az.code.Tour032.repos;

import az.code.Tour032.models.Button;
import az.code.Tour032.models.Language;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

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