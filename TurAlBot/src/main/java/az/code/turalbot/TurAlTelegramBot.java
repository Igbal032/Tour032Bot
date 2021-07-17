package az.code.turalbot;

import az.code.turalbot.cache.Cache;
import az.code.turalbot.cache.ImageCache;
import az.code.turalbot.cache.SessionCash;
import az.code.turalbot.dtos.OfferDTO;
import az.code.turalbot.handlers.TelegramFacade;
import az.code.turalbot.cache.Session;
import az.code.turalbot.models.Button;
import az.code.turalbot.models.Offer;
import az.code.turalbot.repos.ButtonsRepo;
import az.code.turalbot.services.OfferService;
import az.code.turalbot.services.TurAlBotService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.bots.TelegramWebhookBot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Arrays;
import java.util.List;

public class TurAlTelegramBot extends TelegramWebhookBot {

    private String userName;
    private String botToken;
    private String webHookPath;

    @Autowired
    private TurAlBotService turAlBotService;
    @Autowired
    private TelegramFacade telegramFacade;
    @Autowired
    private OfferService offerService;
    @Autowired
    private ButtonsRepo buttonsRepo;
    @Autowired
    private Cache cache;
    @Autowired
    private SessionCash sessionCash;

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
        Session session = sessionCash.findByChatId(offerDTO.getChatId());
        if (cache.findByUUID(offerDTO.getUUID()).getCountOfSendingImage()%5==0
                &&isAccessOrNot(session.getUUID())==false){
            saveImage(offerDTO.getUUID(),offerDTO.getFile());
            offerService.createOffer(offerDTO,null,false);
            if (cache.findByUUID(offerDTO.getUUID()).getButtonId()==null){
                createMoreButton(session);
            }
            return null;
        }
        else {
            try {
                incrCountOfSendingImage(offerDTO.getUUID());
                SendPhoto msg = new SendPhoto()
                        .setChatId(offerDTO.getChatId())
                        .setCaption(offerDTO.getCompanyName())
                        .setPhoto(new InputFile(new ByteArrayInputStream(offerDTO.getFile()),offerDTO.getUUID()));
                Message message = execute(msg);
                offerDTO.toBuilder().messageId(message.getMessageId()).build();
                Integer msjId = createMoreButton(session);
                offerService.createOffer(offerDTO,message.getMessageId(),true);
                return message;
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
            finally {
                return null;
            }
        }
    }

    public boolean isExistOffer(OfferDTO offerDTO){
        return offerService.isExistOffer(offerDTO);
    }

    public SendMessage sendMoreOffer(Session session){
        removeMessage(cache.findByUUID(session.getUUID()).getButtonId(),session.getChatId(),session);
        List<Offer> offerList = offerService.getOffersWithUuidAnIsShow(session.getUUID(),false);
        offerList.forEach(offer->{
            incrCountOfSendingImage(offer.getUUID());
            SendPhoto msg = new SendPhoto()
                    .setChatId(offer.getChatId())
                    .setCaption(offer.getCompanyName())
                    .setPhoto(new InputFile(new ByteArrayInputStream(offer.getFile()),offer.getUUID()));
            try {
                Message message = execute(msg);
                setIsShowOnOffer(offer,message.getMessageId());
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        });
        createMoreButton(session);
        return null;
    }

    public Integer createMoreButton(Session session){
        if (isExistOffer(OfferDTO.builder().UUID(session.getUUID()).build())){
            if (cache.findByUUID(session.getUUID()).getCountOfSendingImage()%5==0){
                InlineKeyboardMarkup inlineKeyboardMarkup = turAlBotService.createButtons(Arrays
                        .asList(buttonsRepo.getButtonWithCallBackAndLangId("more",session.getCurrentLanguage().getId())));
                SendMessage sendMessage = new SendMessage();
                sendMessage.setChatId(session.getChatId());
                sendMessage.setText("-------");
                sendMessage.setReplyMarkup(inlineKeyboardMarkup);
                try {
                    Message message = execute(sendMessage);
                    addButtonIdToCache(session, message.getMessageId());
                    return message.getMessageId();
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    public void addButtonIdToCache(Session session, Integer buttonId){
        ImageCache imageCache = cache.findByUUID(session.getUUID());
        cache.save(ImageCache.builder()
                .chatId(session.getChatId())
                .buttonId(buttonId)
                .UUID(session.getUUID())
                .countOfSendingImage(imageCache.getCountOfSendingImage())
                .countOfAllImages(imageCache.getCountOfAllImages())
                .isAccess(imageCache.isAccess())
                .build());
    }

    public void removeMessage(Integer buttonId, Long chatId,Session session){
        EditMessageReplyMarkup editMessageReplyMarkup = new EditMessageReplyMarkup();
        editMessageReplyMarkup.setMessageId(buttonId);
        editMessageReplyMarkup.setChatId(chatId);
        editMessageReplyMarkup.setReplyMarkup(null);
        addButtonIdToCache(session,null);
        try {
            execute(editMessageReplyMarkup);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public boolean isAccessOrNot(String UUID){
        ImageCache imageCache = cache.findByUUID(UUID);
        return imageCache.isAccess();
    }

    public void setIsShowOnOffer(Offer offer, Integer msjId) {
        offerService.setIsShowOnOffer(offer,msjId);
    }

    public int incrCountOfSendingImage(String UUID){
        ImageCache imgCash = cache.findByUUID(UUID);
        int cnt = imgCash.getCountOfSendingImage();
        ImageCache imageCache = ImageCache.builder()
                .chatId(imgCash.getChatId())
                .countOfAllImages(imgCash.getCountOfAllImages())
                .countOfSendingImage(++cnt)
                .UUID(imgCash.getUUID())
                .isAccess(false)
                .build();
        cache.save(imageCache);
        return imageCache.getCountOfAllImages();
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