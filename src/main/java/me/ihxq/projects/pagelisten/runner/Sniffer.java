package me.ihxq.projects.pagelisten.runner;

import lombok.extern.slf4j.Slf4j;
import me.ihxq.projects.pagelisten.config.RunConfig;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import javax.annotation.PreDestroy;
import java.time.Duration;
import java.util.Objects;

import static org.openqa.selenium.support.ui.ExpectedConditions.presenceOfElementLocated;

/**
 * @author xq.h
 * 2020/4/18 16:19
 **/
@Slf4j
@Service
public class Sniffer {
    private final ChromeDriver driver;

    public Sniffer(RunConfig runConfig) {

        String chromeDriverPath = Objects.requireNonNull(
                this.getClass().getClassLoader().getResource(RunConfig.CHROME_DRIVER_PATH),
                "Driver not found."
        ).getPath();
        System.setProperty("webdriver.chrome.driver", chromeDriverPath);
        ChromeOptions options = new ChromeOptions().addArguments(runConfig.getChromeOptions());
        ChromeDriverService chromeDriverService = new ChromeDriverService.Builder()
                .withSilent(true)
                .build();
        driver = new ChromeDriver(chromeDriverService, options);
        log.info("Chrome driver initialized.");
    }

    @Retryable
    public String sniff(String url, By selector, Duration waitTimeout) {
        log.info("Sniff {} by {}", url, selector);
        driver.get(url);
        WebDriverWait wait = new WebDriverWait(driver, waitTimeout.toSeconds());
        WebElement webElement = wait.until(presenceOfElementLocated(selector));
        scrollIntoView(webElement);
        return webElement.getText();
    }

    @PreDestroy
    public void close() {
        log.info("Try to quit Chrome driver.");
        try {
            driver.quit();
        } catch (Exception e) {
            if (log.isDebugEnabled()) {
                log.debug("Error occurred while quiting chrome driver, driver may not quit", e);
            } else {
                log.warn("Error occurred while quiting chrome driver, driver may not quit");
            }
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
