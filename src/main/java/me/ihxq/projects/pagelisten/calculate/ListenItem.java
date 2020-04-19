package me.ihxq.projects.pagelisten.calculate;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;

import java.time.Duration;
import java.util.Optional;

/**
 * @author xq.h
 * 2020/4/19 14:26
 **/
@Data
@Builder
@Getter(AccessLevel.NONE)
public class ListenItem {
    private String name;
    private String description;
    private String operate;
    private String targetValue;
    private String url;
    private String cssSelector;
    private String xpathSelector;
    private Duration waitTimeout;

    /**
     * default is 10 seconds
     *
     * @return the time out duration of wait for element
     */
    public Duration getWaitTimeout() {
        return waitTimeout == null ? Duration.ofSeconds(10) : waitTimeout;
    }

    public Optional<String> getName() {
        return Optional.ofNullable(this.name);
    }

    public Optional<String> getDescription() {
        return Optional.ofNullable(this.description);
    }

    public Optional<Operator> getOperate() {
        return Operator.of(this.operate);
    }

    public Optional<String> getTargetValue() {
        return Optional.ofNullable(this.targetValue);
    }

    public Optional<String> getUrl() {
        return Optional.ofNullable(this.url);
    }

    public Optional<By> getSelector() {
        if (StringUtils.isNotBlank(this.cssSelector)) {
            return Optional.of(By.cssSelector(this.cssSelector));
        } else if (StringUtils.isNotBlank(this.xpathSelector)) {
            return Optional.of(By.xpath(this.cssSelector));
        } else {
            return Optional.empty();
        }
    }
}
