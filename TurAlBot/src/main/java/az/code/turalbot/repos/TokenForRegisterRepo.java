package az.code.turalbot.repos;

import az.code.turalbot.models.Auth.Confirmation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TokenForRegisterRepo extends JpaRepository<Confirmation,Long> {

    Confirmation getTokenForRegisterByEmail(String email);

    boolean existsByEmailAndToken(String email, String token);
}
