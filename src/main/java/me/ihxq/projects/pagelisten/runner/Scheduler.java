package me.ihxq.projects.pagelisten.runner;

import lombok.extern.slf4j.Slf4j;
import me.ihxq.projects.pagelisten.config.RunConfig;
import me.ihxq.projects.pagelisten.email.ChangeRecord;
import me.ihxq.projects.pagelisten.email.ChangeReport;
import me.ihxq.projects.pagelisten.email.EmailSender;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.mail.MessagingException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author xq.h
 * 2020/4/19 17:48
 **/
@Slf4j
@Service
public class Scheduler {

    private final Checker checker;
    private final EmailSender emailSender;
    private final RunConfig runConfig;

    public Scheduler(Checker checker,
                     EmailSender emailSender,
                     RunConfig runConfig) {
        this.checker = checker;
        this.emailSender = emailSender;
        this.runConfig = runConfig;
    }

    @PostConstruct
    public void initContinuallyCheck() {
        ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(runConfig.getListens().size());
        runConfig.getListens().forEach(v -> {
            scheduledExecutorService.scheduleWithFixedDelay(() -> {
                try {
                    checker.check(v, true);
                } catch (Exception e) {
                    log.error("Failed to check for: {}", v, e);
                }
            }, 3_000, v.getCheckPeriod().toMillis(), TimeUnit.MILLISECONDS);
        });
    }

    @Scheduled(cron = "0 30 8 * * ?")
    public void schedule() throws MessagingException {
        LocalDateTime startTime = LocalDateTime.now();
        List<ChangeRecord> records = runConfig.getListens()
                .stream()
                .map(item -> {
                    try {
                        return checker.check(item, false);
                    } catch (Exception e) {
                        log.error("Failed to check for: {}", item, e);
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        long total = runConfig.getListens().size();
        long success = records.stream().filter(ChangeRecord::isSuccess).count();
        long failure = total - success;
        long hit = records.stream()
                .map(ChangeRecord::getHit)
                .filter(Objects::nonNull)
                .filter(Boolean::booleanValue).count();
        ChangeReport report = ChangeReport.builder()
                .startTime(startTime)
                .endTime(LocalDateTime.now())
                .detail(records)
                .total(total)
                .success(success)
                .failure(failure)
                .hit(hit)
                .build();
        emailSender.send(report);

    }
}
