package az.code.turalbot.daos.intergaces;

import az.code.turalbot.models.Requests;

public interface RequestToAgentDAO {
    void saveRequestForPerAgent(Requests requests);
}
