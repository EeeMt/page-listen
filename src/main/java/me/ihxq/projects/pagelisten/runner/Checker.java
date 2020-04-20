package me.ihxq.projects.pagelisten.runner;

import lombok.extern.slf4j.Slf4j;
import me.ihxq.projects.pagelisten.calculate.ListenItem;
import me.ihxq.projects.pagelisten.calculate.OperateResult;
import me.ihxq.projects.pagelisten.calculate.Operator;
import me.ihxq.projects.pagelisten.config.RunConfig;
import me.ihxq.projects.pagelisten.email.ChangeRecord;
import me.ihxq.projects.pagelisten.email.EmailSender;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriverException;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;

/**
 * @author xq.h
 * 2020/4/18 16:19
 **/
@Slf4j
@Service
public class Checker {
    private final EmailSender emailSender;
    private final RunConfig runConfig;
    private final Sniffer sniffer;

    public Checker(EmailSender emailSender,
                   RunConfig runConfig,
                   Sniffer sniffer) {
        this.emailSender = emailSender;
        this.runConfig = runConfig;
        this.sniffer = sniffer;
    }

    public ChangeRecord check(ListenItem item, boolean sendEmailOnHit) {
        log.info("Check for {}", item);
        By selector = item.getSelector().orElseThrow(() -> new RuntimeException("No selector."));
        String url = item.getUrl().orElseThrow(() -> new RuntimeException("No url."));
        String targetValue = item.getTargetValue().orElseThrow(() -> new RuntimeException("No target value."));
        Operator operator = item.getOperate().orElseThrow(() -> new RuntimeException("No operator."));

        String name = item.getName().orElse("");
        String description = item.getDescription().orElse("");


        boolean success = false;
        String content = null;
        Boolean hit = null;
        String operateResultDescription = null;
        try {
            content = sniffer.sniff(url, selector, item.getWaitTimeout());
            OperateResult operateResult = operator.operate(content, targetValue);
            operateResultDescription = operateResult.getResultDescription();
            hit = operateResult.isHit();
            success = true;
        } catch (WebDriverException e) {
            Throwable cause = e.getCause();
            while (e instanceof TimeoutException) {
                cause = e.getCause();
                if (cause instanceof WebDriverException) {
                    e = (WebDriverException) cause;
                }
            }
            if (cause instanceof WebDriverException) {
                log.error("Failed to process {}, {}", item, cause.getMessage());
            } else {
                log.error("Failed to process {}", item, e);
            }
        } catch (Exception e) {
            log.error("Failed to process {}", item, e);
        }

        ChangeRecord changeRecord = ChangeRecord.builder()
                .name(name)
                .description(description)
                .hit(hit)
                .currentValue(content)
                .operator(operator.description)
                .targetValue(targetValue)
                .success(success)
                .resultDescription(operateResultDescription)
                .build();
        if (sendEmailOnHit) {
            try {
                emailSender.send(changeRecord);
            } catch (MessagingException e) {
                log.error("Failed to send email for hit.", e);
            }
        }
        return changeRecord;
    }
}
