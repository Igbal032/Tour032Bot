package az.code.Tour032;

import az.code.Tour032.cache.Cache;
import az.code.Tour032.cache.ImageCache;
import az.code.Tour032.cache.SessionCache;
import az.code.Tour032.dtos.OfferDTO;
import az.code.Tour032.handlers.TelegramFacade;
import az.code.Tour032.cache.Session;
import az.code.Tour032.models.Agent;
import az.code.Tour032.models.Offer;
import az.code.Tour032.repos.AgentRepo;
import az.code.Tour032.repos.ButtonsRepo;
import az.code.Tour032.services.interfaces.OfferService;
import az.code.Tour032.services.interfaces.Tour032BotService;
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
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.*;
import java.util.Arrays;
import java.util.List;

public class Tour032TelegramBot extends TelegramWebhookBot {

    private String userName;
    private String botToken;
    private String webHookPath;

    @Autowired
    private Tour032BotService tour032BotService;
    @Autowired
    private TelegramFacade telegramFacade;
    @Autowired
    private OfferService offerService;
    @Autowired
    private ButtonsRepo buttonsRepo;
    @Autowired
    private Cache cache;
    @Autowired
    private SessionCache sessionCache;
    @Autowired
    private AgentRepo agentRepo;

    public Tour032TelegramBot(Tour032BotService tour032BotService) {
        this.tour032BotService = tour032BotService;
    }

    public Tour032TelegramBot(DefaultBotOptions defaultBotOptions) {
        super(defaultBotOptions);
    }

    @Override
    public BotApiMethod onWebhookUpdateReceived(Update update) {
        final BotApiMethod<?>  replyBot =  telegramFacade.handlerUpdate(update);
        return replyBot;
    }

    @RabbitListener(queues = "offerQueue")
    public Message sendPhotoToTelegram(OfferDTO offerDTO) throws IOException {
        Session session = sessionCache.findByChatId(offerDTO.getChatId());
        System.out.println(offerDTO.getAgentId()+ "Agent ID");
        Agent agent = agentRepo.getById(offerDTO.getAgentId());
        if (cache.findByUUID(offerDTO.getUUID()).getCountOfSendingImage()%5==0
                &&isAccessOrNot(session.getUUID())==false){
            offerService.createOffer(offerDTO,null,false);
            if (cache.findByUUID(offerDTO.getUUID()).getButtonId()==null){
                createMoreButton(session);
            }
            return null;
        }
        else {
            try {
                Integer cnt = incrCountOfSendingImage(offerDTO.getUUID());
                if (cnt==1){
                   execute(tour032BotService.returnNotification(session.getChatId(),"selectOffer"));
                }
                SendPhoto msg = new SendPhoto()
                        .setChatId(offerDTO.getChatId())
                        .setCaption(agent.getCompanyName())
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
                    .setCaption(offer.getAgent().getCompanyName())
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
                InlineKeyboardMarkup inlineKeyboardMarkup = tour032BotService.createButtons(Arrays
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
        return imageCache.getCountOfSendingImage();
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