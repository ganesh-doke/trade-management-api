package com.example.api.trade.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

@Configuration
@ConditionalOnProperty(name = "db.instance.name", havingValue = "dynamodb")
public class DynamoDBConfig {

    @Bean
    public DynamoDbClient dynamoDBClient() {

        return DynamoDbClient.builder()
                .region(Region.AP_SOUTH_1)
                .credentialsProvider(DefaultCredentialsProvider.builder().build())
                .build();
    }

    @Bean
    public DynamoDbEnhancedClient dynamoDbEnhancedClient(DynamoDbClient dynamoDBClient) {
        return DynamoDbEnhancedClient.builder()
                .dynamoDbClient(dynamoDBClient)
                .build();
    }
}
