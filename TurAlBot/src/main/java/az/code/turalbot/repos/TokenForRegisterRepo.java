package az.code.turalbot.repos;

import az.code.turalbot.models.Auth.TokenForRegister;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TokenForRegisterRepo extends JpaRepository<TokenForRegister,Long> {

    TokenForRegister getTokenForRegisterByEmail(String email);
}
