package com.example.api.trade.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("mongodb")
@Getter
@Setter
public class MongoDbConfig {

    private String url;
    private String dbName;
    private String serviceName;
    private String username;
    private String password;
    private int connectionTimeout;
    private int socketTimeout;
    private int heartbeatFrequency;
    private int heartbeatTimeout;
    private int maxWaitTime;
    private int maxConnectionIdleTime;
    private int maxConnectionLifeTime;
    private int maxSize;
    private int minSize;
}
