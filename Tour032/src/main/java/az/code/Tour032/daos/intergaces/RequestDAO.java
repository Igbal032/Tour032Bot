package az.code.Tour032.daos.intergaces;

import az.code.Tour032.models.Requests;

import java.util.List;


public interface RequestDAO {
    Requests deactivateStatus(String UUID);
    Requests getWithUUID(String UUID);
    Requests save(Requests requests);
    List<Requests> getRequestsWithStatus(String status);
    List<Requests> getAllRequest();
}
