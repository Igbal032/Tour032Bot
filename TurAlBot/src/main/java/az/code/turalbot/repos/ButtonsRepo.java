package az.code.turalbot.repos;

import az.code.turalbot.models.Button;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ButtonsRepo extends JpaRepository<Button, Long> {
    @Query("select b from Button b where b.language.id=:lId and b.question.id=:qId")
    List<Button> getButtons(long qId, Long lId);
    @Query(value = "SELECT * FROM buttons b where b.question_id=:qId and language_id is NULL", nativeQuery = true)
    List<Button> getLangButtons(long qId);

//    List<Button> getButtonsByQuestionIdAAndLanguageId(Long qId, Long lId);
}
