package az.code.Tour032.repos;

import az.code.Tour032.models.Question;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuestionsRepo extends JpaRepository<Question, Long> {
}
