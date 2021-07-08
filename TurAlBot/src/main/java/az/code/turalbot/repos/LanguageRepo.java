package az.code.turalbot.repos;

import az.code.turalbot.models.Language;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LanguageRepo extends JpaRepository<Language, Long> {
}
