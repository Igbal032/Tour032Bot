package az.code.Tour032.repos;

import az.code.Tour032.models.Language;
import org.springframework.data.jpa.repository.JpaRepository;


public interface LanguageRepo extends JpaRepository<Language, Long> {
}
