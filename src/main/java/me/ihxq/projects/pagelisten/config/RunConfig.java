package me.ihxq.projects.pagelisten.config;

import lombok.Data;
import me.ihxq.projects.pagelisten.calculate.ListenItem;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author xq.h
 * 2019/12/21 15:55
 **/
@Data
@ConfigurationProperties(prefix = "run")
public class RunConfig {
    public static final String CHROME_DRIVER_PATH = "driver/chromedriver";

    private List<String> chromeOptions = Stream.of(
            "--window-size=1920,1080",
            //"--blink-settings=imagesEnabled=false",
            "--disable-gpu",
            "--headless",
            "--no"
    ).collect(Collectors.toList());

    private List<ListenItem> listens = Stream.of(
            ListenItem.builder()
                    .cssSelector("#root > div > section > div:nth-child(7) > a > div.price")
                    .name("Surge 4")
                    .description("less than $49.99")
                    .operate("lt")
                    .targetValue("$49.99")
                    .url("https://nssurge.com/buy_now")
                    .build()
    ).collect(Collectors.toList());
}
