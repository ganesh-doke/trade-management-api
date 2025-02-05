package com.example.api.trade.config;

import com.mongodb.MongoClientSettings;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.WriteConcern;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.concurrent.TimeUnit;

@Slf4j
@Configuration
public class MongoClientConfig {

    private static final String SEMICOLON = ";";
    private static final String COLON = ":";

    final MongoDbConfig mongoDbConfig;

    public MongoClientConfig(MongoDbConfig mongoDbConfig) {
        this.mongoDbConfig = mongoDbConfig;
    }

    @Bean (
            name = {"mongoClient"}, destroyMethod = "close"
    )
    public MongoClient mongoClient() {

        log.info("mongoClient configuration to start");

        List<MongoCredential> mongoCredentialList = new ArrayList<>();
        MongoCredential mongoCredential = MongoCredential.createScramSha1Credential(
                mongoDbConfig.getUsername(),
                mongoDbConfig.getDbName(),
                mongoDbConfig.getPassword().toCharArray());

        mongoCredentialList.add(mongoCredential);

        MongoClientSettings mongoClientSettings = MongoClientSettings.builder()
                .credential(mongoCredential)
                .applyToSslSettings(builder -> {
                    builder.enabled(Boolean.FALSE);
                    builder.invalidHostNameAllowed(Boolean.TRUE);
                })
                .applyToClusterSettings(builder -> builder.hosts(getServerAddressList()))
                .applyToSocketSettings(builder -> {
                    builder.readTimeout(mongoDbConfig.getSocketTimeout(), TimeUnit.MILLISECONDS);
                    builder.connectTimeout(mongoDbConfig.getConnectionTimeout(), TimeUnit.MILLISECONDS);
                })
                .applyToServerSettings(builder -> builder.heartbeatFrequency(mongoDbConfig.getHeartbeatFrequency(), TimeUnit.MILLISECONDS))
                .applyToConnectionPoolSettings(builder -> {
                    builder.maxConnectionIdleTime(mongoDbConfig.getMaxConnectionIdleTime(), TimeUnit.MILLISECONDS);
                    builder.maxWaitTime(mongoDbConfig.getMaxWaitTime(), TimeUnit.MILLISECONDS);
                    builder.maxConnectionLifeTime(mongoDbConfig.getMaxConnectionLifeTime(), TimeUnit.MILLISECONDS);
                    builder.maxSize(mongoDbConfig.getMaxSize());
                    builder.minSize(mongoDbConfig.getMinSize());
                })
                .writeConcern(WriteConcern.ACKNOWLEDGED)
                .build();

        MongoClient mongoClient = MongoClients.create(mongoClientSettings);
        log.info("mongoClient configuration to end");

        return mongoClient;
    }

    /**
     * Build Mongo DB Server address list
     * @return List<ServerAddress>
     */
    private List<ServerAddress> getServerAddressList() {

        List<ServerAddress> serverAddressList = new ArrayList<>();

        StringTokenizer urlTokenizer = new StringTokenizer(mongoDbConfig.getUrl(), SEMICOLON);
        while (urlTokenizer.hasMoreTokens()) {

            StringTokenizer url = new StringTokenizer(urlTokenizer.nextToken(), COLON);

            String host = url.nextToken();
            int port = Integer.parseInt(url.nextToken());
            serverAddressList.add(new ServerAddress(host, port));
        }

        return serverAddressList;
    }
}
