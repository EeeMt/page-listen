package me.ihxq.projects.pagelisten.runner;

import lombok.extern.slf4j.Slf4j;
import me.ihxq.projects.pagelisten.calculate.ListenItem;
import me.ihxq.projects.pagelisten.calculate.OperateResult;
import me.ihxq.projects.pagelisten.calculate.Operator;
import me.ihxq.projects.pagelisten.config.RunConfig;
import me.ihxq.projects.pagelisten.email.ChangeRecord;
import me.ihxq.projects.pagelisten.email.EmailSender;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import javax.annotation.PreDestroy;
import java.util.Objects;

import static org.openqa.selenium.support.ui.ExpectedConditions.presenceOfElementLocated;

/**
 * @author xq.h
 * 2020/4/18 16:19
 **/
@Slf4j
@Service
public class Checker {
    private final EmailSender emailSender;
    private final RunConfig runConfig;
    private final ChromeDriver driver;

    public Checker(EmailSender emailSender, RunConfig runConfig) {
        this.emailSender = emailSender;
        this.runConfig = runConfig;

        String chromeDriverPath = Objects.requireNonNull(this.getClass().getClassLoader().getResource(RunConfig.CHROME_DRIVER_PATH), "Driver not found.")
                .getPath();
        System.setProperty("webdriver.chrome.driver", chromeDriverPath);
        ChromeOptions options = new ChromeOptions().addArguments(this.runConfig.getChromeOptions());
        ChromeDriverService chromeDriverService = new ChromeDriverService.Builder()
                .withSilent(true)
                .build();
        driver = new ChromeDriver(chromeDriverService, options);
        log.info("Chrome driver initialized.");
    }

    @Retryable
    public ChangeRecord check(ListenItem item) {
        log.info("Check for {}", item);
        By selector = item.getSelector().orElseThrow(() -> new RuntimeException("No selector."));
        String url = item.getUrl().orElseThrow(() -> new RuntimeException("No url."));
        String targetValue = item.getTargetValue().orElseThrow(() -> new RuntimeException("No target value."));
        Operator operator = item.getOperate().orElseThrow(() -> new RuntimeException("No operator."));

        String name = item.getName().orElse("");
        String description = item.getDescription().orElse("");

        WebDriverWait wait = new WebDriverWait(driver, item.getWaitTimeout().toSeconds());

        driver.get(url);
        WebElement webElement = wait.until(presenceOfElementLocated(selector));
        scrollIntoView(webElement);
        String content = webElement.getText();

        OperateResult operateResult = operator.operate(content, targetValue);

        driver.quit();
        return ChangeRecord.builder()
                .name(name)
                .description(description)
                .hit(operateResult.isHit())
                .currentValue(content)
                .operator(operator.description)
                .targetValue(targetValue)
                .resultDescription(operateResult.getResultDescription())
                .build();
    }

    @PreDestroy
    public void close() {
        log.info("Try to quit Chrome driver.");
        try {
            driver.quit();
        } catch (Exception e) {
            log.error("Failed to quit chrome driver.", e);
        }
        log.info("Chrome driver quited.");
    }

    private void scrollIntoView(WebElement webElement) {
        if (driver != null) {
            driver.executeScript("arguments[0].scrollIntoView(true);", webElement);
        } else {
            log.warn("Driver is null.");
        }
    }
}
