package az.code.turalbot.repos;

import az.code.turalbot.models.Language;
import org.springframework.data.jpa.repository.JpaRepository;


public interface LanguageRepo extends JpaRepository<Language, Long> {
}
