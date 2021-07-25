package az.code.turalbot.daos.intergaces;

import az.code.turalbot.models.Requests;


public interface RequestDAO {
    Requests deactivateStatus(String UUID);
    Requests getWithUUID(String UUID);
    Requests save(Requests requests);
}
