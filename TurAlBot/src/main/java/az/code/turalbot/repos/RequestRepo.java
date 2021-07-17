package az.code.turalbot.repos;

import az.code.turalbot.models.Requests;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface RequestRepo extends JpaRepository<Requests,Long> {

    @Query("select r from Requests r where r.UUID=:UUID and r.isActive=:isActive")
    Requests getRequestByIsActiveAndChatId(String UUID, boolean isActive);

    Requests getRequestsByUUID(String UUID);
}
