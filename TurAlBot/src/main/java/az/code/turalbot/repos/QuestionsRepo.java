package az.code.turalbot.repos;

import az.code.turalbot.models.Question;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuestionsRepo extends JpaRepository<Question, Long> {
}
