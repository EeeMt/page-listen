package me.ihxq.projects.pagelisten.email;

import lombok.extern.slf4j.Slf4j;
import me.ihxq.projects.pagelisten.config.SenderConfig;
import org.springframework.stereotype.Service;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.util.Properties;

/**
 * @author xq.h
 * 2020/4/18 14:47
 **/
@Slf4j
@Service
public class EmailSender {
    private final SenderConfig senderConfig;

    private static final Properties PROPERTIES = new Properties() {{
        put("mail.smtp.auth", true);
        put("mail.smtp.starttls.enable", "true");
        put("mail.smtp.host", "smtp.qiye.aliyun.com");
        put("mail.smtp.port", "25");
        put("mail.smtp.ssl.trust", "smtp.qiye.aliyun.com");
    }};

    public EmailSender(SenderConfig senderConfig) {
        this.senderConfig = senderConfig;
    }

    public void send(String content) throws MessagingException {

        log.info("Try sending email.");
        log.debug("Email content: {}", content);
        Session session = Session.getInstance(PROPERTIES, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(senderConfig.getSenderAddress(), senderConfig.getSenderPassword());
            }
        });
        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(senderConfig.getSenderAddress()));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(senderConfig.getToAddress()));

        message.setSubject("Notice");

        MimeBodyPart mimeBodyPart = new MimeBodyPart();
        mimeBodyPart.setContent(content, "text/html");

        Multipart multipart = new MimeMultipart();
        multipart.addBodyPart(mimeBodyPart);

        message.setContent(multipart);

        Transport.send(message);
        log.info("Email successfully sent.");
    }
}
