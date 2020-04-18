package me.ihxq.projects.pagelisten.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author xq.h
 * 2020/4/18 15:37
 **/
@Data
@ConfigurationProperties(prefix = "sender")
public class SenderConfig {
    private String toAddress;
    private String senderAddress;
    private String senderPassword;
}
