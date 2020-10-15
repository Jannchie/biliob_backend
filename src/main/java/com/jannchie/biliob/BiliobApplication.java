package com.jannchie.biliob;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.session.data.mongo.config.annotation.web.http.EnableMongoHttpSession;

/**
 * @author jannchie
 */
@EnableCaching
@EnableScheduling
@EnableMongoHttpSession(maxInactiveIntervalInSeconds = 2592000)
@SpringBootApplication
@ConditionalOnProperty(
        value = "app.scheduling.enable", havingValue = "true", matchIfMissing = true
)
public class BiliobApplication {
    public static void main(String[] args) {
        SpringApplication.run(BiliobApplication.class, args);
    }
}
