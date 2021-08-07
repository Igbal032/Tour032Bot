package az.code.Tour032.repos;

import az.code.Tour032.models.Requests;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface RequestRepo extends JpaRepository<Requests,Long> {

    Requests getRequestsByUUID(String UUID);

    @Query("select r from Requests r where r.requestStatus=:status")
    List<Requests> getAllRequestsWithActive(String status);

    @Query("select r from Requests r where r.isActive=:isActive")
    List<Requests> getAllRequests(boolean isActive);

}
