package com.tqz.business.utils;

import com.mongodb.MongoClient;
import kafka.Kafka;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.Jedis;

@Configuration
public class Configure {

    public Configure() {
    }

    @Bean(name = "mongoClient")
    public MongoClient getMongoClient() {
        return new MongoClient(PropertiesFileUtils.MONGO_HOST, PropertiesFileUtils.MONGO_PORT);
    }

    @Bean(name = "jedis")
    public Jedis getRedisClient() {
        return new Jedis(PropertiesFileUtils.REDIS_HOST, PropertiesFileUtils.REDIS_PORT);
    }

    @Bean(name = "Kafka")
    public Kafka getKafkaClient() { return new Kafka(PropertiesFileUtils.KAFKA_HOST, PropertiesFileUtils.KAFKA_PORT); }
}
