package az.code.turalbot.services;

import az.code.turalbot.Exceptions.RequestNotFoundException;
import az.code.turalbot.daos.intergaces.OfferDAO;
import az.code.turalbot.daos.intergaces.RequestDAO;
import az.code.turalbot.dtos.ImageDTO;
import az.code.turalbot.dtos.OfferDTO;
import az.code.turalbot.models.Agent;
import az.code.turalbot.models.Offer;
import az.code.turalbot.models.Requests;
import az.code.turalbot.services.interfaces.RequestService;
import az.code.turalbot.services.interfaces.TurAlBotService;
import az.code.turalbot.utils.Utils;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
@RequiredArgsConstructor
public class RequestServiceImp implements RequestService {

    private final RequestDAO requestDAO;
    private final OfferDAO offerDAO;
    private final RabbitTemplate template;
    private final TopicExchange exchange;
    private final TurAlBotService turAlBotService;
    @Value("${sample.rabbitmq.offerKey}")
    String offerKey;
    @Override
    public Requests getRequestWithUUID(String UUID) {
        return requestDAO.getWithUUID(UUID);
    }

    @Override
    public String sendDataToRabBitMQ(String UUID, ImageDTO imageDTO, Agent agent) throws IOException {
        Requests requests = getRequestWithUUID(UUID);
        if (requests==null){
            throw new RequestNotFoundException("Request not found!!");
        }
        if (!requests.isActive()){
            return "Request is deActive";
        }
        if (!isOfferSend(UUID,agent)){
            Path imgPath = Paths.get(Utils.generateImageBasedOnText(imageDTO));
            byte[] fileContent = Files.readAllBytes(imgPath);
            OfferDTO offer = OfferDTO.builder()
                    .UUID(UUID)
                    .agentId(agent.getId())
                    .chatId(requests.getChatId())
                    .file(fileContent)
                    .build();
            template.convertAndSend(exchange.getName(),offerKey,offer);
            return "Offer was sent";
        }
        return "You have already sent offer";
    }

    public boolean isOfferSend(String UUID, Agent agent){
        Offer offer = offerDAO.hasOffer(agent,UUID);
        if(offer==null){
            return false;
        }
        return true;
    }

}
