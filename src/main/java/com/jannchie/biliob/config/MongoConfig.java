package com.jannchie.biliob.config;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.MongoTransactionManager;
import org.springframework.data.mongodb.core.MongoTemplate;

@Configuration
class MongoConfig implements EnvironmentAware {

    private static String BILIOB_MONGO_URL;

    @Bean
    MongoTransactionManager transactionManager(MongoDatabaseFactory dbFactory) {
        return new MongoTransactionManager(dbFactory);
    }

    /**
     * Use the Reactive Streams Mongo Client API to create a
     * com.mongodb.reactivestreams.client.MongoClient instance.
     */
    public @Bean
    MongoClient reactiveMongoClient() {
        return MongoClients.create(MongoConfig.BILIOB_MONGO_URL);
    }

    public @Bean
    MongoTemplate mongoTemplate(MongoDatabaseFactory mongoDatabaseFactory) {
        return new MongoTemplate(mongoDatabaseFactory);
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
