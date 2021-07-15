package az.code.turalbot.services;

import az.code.turalbot.models.Requests;
import org.springframework.web.multipart.MultipartFile;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;

import javax.ws.rs.core.Response.*;
import java.io.IOException;

public interface RequestService {

    Requests getRequestWithUUID(String UUID);

    ResponseBuilder sendDataToRabBitMQ(String UUID, MultipartFile file) throws IOException;
}
