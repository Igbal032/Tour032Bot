package az.code.Tour032.repos;

import az.code.Tour032.models.Action;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ActionsRepo extends JpaRepository<Action, Long> {

    @Query("select a from Action a where a.question.id=:qId")
    Action findActionWithQId(Long qId);

    Action findActionByType(String aType);
}
