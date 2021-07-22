package az.code.turalbot.services.interfaces;

import az.code.turalbot.dtos.ImageDTO;
import az.code.turalbot.models.Agent;
import az.code.turalbot.models.Requests;
import org.springframework.web.multipart.MultipartFile;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;

import javax.ws.rs.core.Response.*;
import java.io.IOException;

public interface RequestService {

    Requests getRequestWithUUID(String UUID);

    String sendDataToRabBitMQ(String UUID, ImageDTO imageDTO, Agent agent) throws IOException;
}
