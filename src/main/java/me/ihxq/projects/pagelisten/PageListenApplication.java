package me.ihxq.projects.pagelisten;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableRetry
@EnableScheduling
@ConfigurationPropertiesScan
@SpringBootApplication
public class PageListenApplication {

    public static void main(String[] args) {
        SpringApplication.run(PageListenApplication.class, args);
    }

}
