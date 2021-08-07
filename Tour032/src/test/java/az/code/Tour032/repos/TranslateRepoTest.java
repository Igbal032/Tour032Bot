package az.code.Tour032.repos;

import az.code.Tour032.models.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
class TranslateRepoTest {
    @Autowired
    private LanguageRepo languageRepo;
    @Autowired
    private QuestionsRepo questionsRepo;
    @Autowired
    private TranslateRepo translateRepo;
    @Test
    void getTranslate() {

        Language language = Language.builder().languageName("AZERBAYCAN DILI")
                .build();
        languageRepo.save(language);
        Question question = Question.builder().content("There is a test content")
                .keyWord("test").regex("[0-3][0-9]-[0-1][0-9]-[\\d]{4}")
                .build();
        questionsRepo.save(question);

        Translate translate = Translate.builder()
                .language(language).question(question).translatedContent("Content Tercume Edildi!!")
                .build();
        translateRepo.save(translate);
        //when
        Translate expected = translateRepo.getTranslate(language.getId(),question.getId());
        //then
        assertThat(translate.getLanguage().getLanguageName())
                .isEqualTo(expected.getLanguage().getLanguageName());
        assertThat(translate.getQuestion().getRegex())
                .isEqualTo(expected.getQuestion().getRegex());
        assertThat(translate.getTranslatedContent())
                .isEqualTo(expected.getTranslatedContent());
    }
}