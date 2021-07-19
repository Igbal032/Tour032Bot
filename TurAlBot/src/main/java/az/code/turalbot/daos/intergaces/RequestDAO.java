package az.code.turalbot.daos.intergaces;

import az.code.turalbot.dtos.OfferDTO;
import az.code.turalbot.models.Requests;

public interface RequestDAO {
    Requests deactivateStatus(String UUID);
    Requests getWithUUID(String UUID);
    Requests saveRequest(Long chatId, String jsonText,String UUID);
}
