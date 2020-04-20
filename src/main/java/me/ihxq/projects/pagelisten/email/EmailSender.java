package me.ihxq.projects.pagelisten.email;

import lombok.extern.slf4j.Slf4j;
import me.ihxq.projects.pagelisten.config.SenderConfig;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Service;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.time.LocalDate;
import java.util.Properties;

import static me.ihxq.projects.pagelisten.config.RetryTemplateConfig.TASK_NAME_ATTR_NAME;

/**
 * @author xq.h
 * 2020/4/18 14:47
 **/
@Slf4j
@Service
public class EmailSender {
    private final SenderConfig senderConfig;
    private final EmailContentGenerator emailContentGenerator;
    private final RetryTemplate retryTemplate;

    private static final Properties PROPERTIES = new Properties() {{
        put("mail.smtp.auth", true);
        put("mail.smtp.starttls.enable", "true");
        put("mail.smtp.host", "smtp.qiye.aliyun.com");
        put("mail.smtp.port", "25");
        put("mail.smtp.ssl.trust", "smtp.qiye.aliyun.com");
    }};

    public EmailSender(SenderConfig senderConfig,
                       EmailContentGenerator emailContentGenerator,
                       RetryTemplate retryTemplate) {
        this.senderConfig = senderConfig;
        this.emailContentGenerator = emailContentGenerator;
        this.retryTemplate = retryTemplate;
    }

    public void send(ChangeReport report) throws MessagingException {
        retryTemplate.execute(context -> {
            context.setAttribute(TASK_NAME_ATTR_NAME, "Send hit email");
            this.send("Notice of " + LocalDate.now(), emailContentGenerator.serializeToHtml(report));
            return null;
        });
    }

    public void send(ChangeRecord record) throws MessagingException {
        retryTemplate.execute(context -> {
            context.setAttribute(TASK_NAME_ATTR_NAME, "Send report email");
            this.send("Hit " + record.getName() + "!", emailContentGenerator.serializeToHtml(record));
            return null;
        });
    }

    private void send(String title, String content) throws MessagingException {

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

        message.setSubject(title);

        MimeBodyPart mimeBodyPart = new MimeBodyPart();
        mimeBodyPart.setContent(content, "text/html");

        Multipart multipart = new MimeMultipart();
        multipart.addBodyPart(mimeBodyPart);

        message.setContent(multipart);

        Transport.send(message);
        log.info("Email successfully sent.");
    }
}
