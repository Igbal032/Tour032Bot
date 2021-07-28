package az.code.turalbot.daos.intergaces;

import az.code.turalbot.models.Requests;

import java.util.List;


public interface RequestDAO {
    Requests deactivateStatus(String UUID);
    Requests getWithUUID(String UUID);
    Requests save(Requests requests);
    List<Requests> getRequestsWithStatus(String status);
}
