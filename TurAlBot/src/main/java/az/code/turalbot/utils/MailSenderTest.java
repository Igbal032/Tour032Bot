package az.code.turalbot.utils;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MailSenderTest {

    private final JavaMailSender javaMailSender;

    public  void  sendEmail(String to, String subject,String content) {
        System.out.println(javaMailSender);
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setTo(to);
        msg.setSubject(subject);
        msg.setText(content);
        javaMailSender.send(msg);
        System.out.println("Succesfully sended");
    }
}
