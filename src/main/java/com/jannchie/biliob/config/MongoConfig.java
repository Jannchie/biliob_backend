package com.jannchie.biliob.config;

import com.mongodb.MongoClientOptions;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.MongoTransactionManager;

@Configuration
class MongoConfig implements EnvironmentAware {

    private static String BILIOB_MONGO_URL;

    @Bean
    MongoTransactionManager transactionManager(MongoDbFactory dbFactory) {
        return new MongoTransactionManager(dbFactory);
    }

    @Bean
    public MongoClientOptions mongoOptions() {
        return MongoClientOptions.builder()
                .maxConnectionIdleTime(30000)
                .serverSelectionTimeout(3000)
                .maxConnectionLifeTime(600000)
                .connectTimeout(10000)
                .maxWaitTime(5000)
                .socketTimeout(10000)
                .build();
    }

    /**
     * Use the Reactive Streams Mongo Client API to create a
     * com.mongodb.reactivestreams.client.MongoClient instance.
     */
    public @Bean
    MongoClient reactiveMongoClient() {
        return MongoClients.create(MongoConfig.BILIOB_MONGO_URL);
    }

    /**
     * Set the {@code Environment} that this component runs in.
     *
     * @param environment 环境变量
     */
    @Override
    public void setEnvironment(Environment environment) {
        MongoConfig.BILIOB_MONGO_URL = environment.getProperty("BILIOB_MONGO_URL");
    }
}
