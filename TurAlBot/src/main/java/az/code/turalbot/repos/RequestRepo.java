package az.code.turalbot.repos;

import az.code.turalbot.models.Requests;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RequestRepo extends JpaRepository<Requests,Long> {
}
