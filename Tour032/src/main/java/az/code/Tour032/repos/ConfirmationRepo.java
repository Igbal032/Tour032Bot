package az.code.Tour032.repos;

import az.code.Tour032.models.Auth.Confirmation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConfirmationRepo extends JpaRepository<Confirmation,Long> {

    Confirmation getConfirmationByEmailAndConfirmNumber(String email, int number);

    boolean existsByEmailAndConfirmNumber(String email, String token);
}
