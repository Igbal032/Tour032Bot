package az.code.turalbot.repos;

import az.code.turalbot.models.Action;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ActionsRepo extends JpaRepository<Action, Long> {
//    @Query("select a from Action a where a.question.id=:qId and a.language.id=:langId")
//    Action findActionWithQId(Long qId,Long langId);
    @Query("select a from Action a where a.question.id=:qId")
    Action findActionWithQId(Long qId);
}