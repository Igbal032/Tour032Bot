package az.code.turalbot;

import az.code.turalbot.cache.Cash;
import az.code.turalbot.dtos.OfferDTO;
import az.code.turalbot.handlers.TelegramFacade;
import az.code.turalbot.models.Offer;
import az.code.turalbot.models.Session;
import az.code.turalbot.repos.OfferRepo;
import az.code.turalbot.services.OfferService;
import az.code.turalbot.services.SessionService;
import az.code.turalbot.services.TurAlBotService;
import org.apache.commons.io.IOUtils;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.web.multipart.MultipartFile;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.bots.TelegramWebhookBot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.updates.SetWebhook;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import javax.imageio.ImageIO;
import javax.imageio.stream.FileImageOutputStream;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;

public class TurAlTelegramBot extends TelegramWebhookBot {

    private String userName;
    private String botToken;
    private String webHookPath;

//    @Autowired
    private TurAlBotService turAlBotService;
    @Autowired
    private TelegramFacade telegramFacade;
    @Autowired
    private OfferService offerService;

    @Autowired
    private Cash cash;

    public TurAlTelegramBot(TurAlBotService turAlBotService) {
        this.turAlBotService = turAlBotService;
    }

    public TurAlTelegramBot(DefaultBotOptions defaultBotOptions) {
        super(defaultBotOptions);
    }

    @Override
    public BotApiMethod onWebhookUpdateReceived(Update update) {
        final BotApiMethod<?>  replyBot =  telegramFacade.handlerUpdate(update);
        return replyBot;
    }

    @RabbitListener(queues = "senderQueue")
    public Message sendPhotoToTelegram(OfferDTO offerDTO) throws IOException {
        Integer count = cash.findByChatId(offerDTO.getChatId());
        count++;
        Integer getCount = cash.save(offerDTO.getChatId(),count);
        System.out.println(getCount+" is increasing");
        if (getCount>5){
            saveImage(offerDTO.getUUID(),offerDTO.getFile());
            offerService.createOffer(offerDTO,null,false);
            return null;
        }
        else {
            try {
                SendPhoto msg = new SendPhoto()
                        .setChatId(offerDTO.getChatId())
                        .setCaption(offerDTO.getCompanyName())
                        .setPhoto(new InputFile(new ByteArrayInputStream(offerDTO.getFile()),offerDTO.getUUID()));
                Message message = execute(msg);
                offerDTO.toBuilder().messageId(message.getMessageId()).build();
                System.out.println(message.getMessageId()+" Message ID from message");
                offerService.createOffer(offerDTO,message.getMessageId(),true);
                return message;
            } catch (TelegramApiException e) {
                System.out.println("Mesaj gondermesinde xeta bash verdi!!");
                e.printStackTrace();
            }
            finally {
                return null;
            }
        }
    }

    public void saveImage(String name, byte[] imgByte) throws IOException {
        BufferedImage img = ImageIO.read(new ByteArrayInputStream(imgByte));
        File f = new File("image/"+name+".jpg");
        ImageIO.write(img, "jpg", f);
    }
    @Override
    public String getBotUsername() {
        return userName;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }

    @Override
    public String getBotPath() {
        return null;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setBotToken(String botToken) {
        this.botToken = botToken;
    }

    public void setWebHookPath(String webHookPath) {
        this.webHookPath = webHookPath;
    }
}