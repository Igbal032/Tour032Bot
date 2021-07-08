package az.code.turalbot.repos;

import az.code.turalbot.models.Translate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface TranslateRepo extends JpaRepository<Translate, Long> {
    @Query("select t from Translate t where t.language.id=:lang and t.question.id=:questionId")
    Translate getTranslate(long lang, long questionId);
}
