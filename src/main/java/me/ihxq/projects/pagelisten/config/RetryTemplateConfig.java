package me.ihxq.projects.pagelisten.config;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.RetryCallback;
import org.springframework.retry.RetryContext;
import org.springframework.retry.listener.RetryListenerSupport;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;

/**
 * @author xq.h
 * 2019/10/10 16:53
 **/
@Slf4j
@Data
@Configuration
public class RetryTemplateConfig {
    public static final String TASK_NAME_ATTR_NAME = "TASK_NAME_ATTR_NAME";
    private static final int MAX_ATTEMPTS = 3;

    @Bean
    public RetryTemplate customRetryTemplate() {
        RetryTemplate retryTemplate = new RetryTemplate();
        retryTemplate.registerListener(new DefaultRetryListener());
        retryTemplate.setRetryPolicy(new SimpleRetryPolicy(MAX_ATTEMPTS));
        return retryTemplate;
    }

    public static class DefaultRetryListener extends RetryListenerSupport {

        @Override
        public <T, E extends Throwable> void close(RetryContext context,
                                                   RetryCallback<T, E> callback, Throwable throwable) {
            if (context.getRetryCount() != 0) {
                log.info("Retry: done for task: {}", context.getAttribute(TASK_NAME_ATTR_NAME));
            }
            super.close(context, callback, throwable);
        }

        @Override
        public <T, E extends Throwable> void onError(RetryContext context,
                                                     RetryCallback<T, E> callback, Throwable throwable) {
            log.info("Retry: try {}/{} for task: {}", context.getRetryCount(), MAX_ATTEMPTS, context.getAttribute(TASK_NAME_ATTR_NAME));
            log.debug("Retry: error: {}", context.getAttribute(TASK_NAME_ATTR_NAME), throwable);
            super.onError(context, callback, throwable);
        }

        @Override
        public <T, E extends Throwable> boolean open(RetryContext context,
                                                     RetryCallback<T, E> callback) {
            return super.open(context, callback);
        }
    }
}
