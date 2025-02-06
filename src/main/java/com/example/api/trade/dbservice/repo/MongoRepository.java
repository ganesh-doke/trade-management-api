package com.example.api.trade.dbservice.repo;

import com.example.api.trade.config.MongoDbConfig;
import com.mongodb.*;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoDatabase;
import lombok.Getter;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

public abstract class MongoRepository {

    @Autowired protected MongoClient mongoClient;
    @Autowired private MongoDbConfig mongoDbConfig;
    @Autowired private MappingMongoConverter mappingMongoConverter;

    @Getter
    private MongoDatabase mongoDatabase;

    private @PostConstruct void init() {

        CodecRegistry codecRegistry = CodecRegistries.fromRegistries(
                MongoClientSettings.getDefaultCodecRegistry(),
                CodecRegistries.fromProviders(
                        PojoCodecProvider.builder().automatic(Boolean.TRUE).build()
                )
        );

        mongoDatabase = mongoClient
                .getDatabase(mongoDbConfig.getDbName())
                .withCodecRegistry(codecRegistry)
                .withReadPreference(ReadPreference.primary())
                .withReadConcern(ReadConcern.DEFAULT)
                .withWriteConcern(WriteConcern.ACKNOWLEDGED);
    }

    public BasicDBObject getBasicDBObject(Object object) {

        BasicDBObject basicDBObject = new BasicDBObject();
        mappingMongoConverter.write(object, basicDBObject);
        return basicDBObject;
    }

    public List<BasicDBObject> getBasicDbObjectList(List<?> list) {

        if (CollectionUtils.isEmpty(list)) return null;

        List<BasicDBObject> basicDBObjects = new ArrayList<>();

        for (Object object : list) {
            BasicDBObject basicDBObject = new BasicDBObject();
            mappingMongoConverter.write(object, basicDBObject);
            basicDBObjects.add(basicDBObject);
        }
        return basicDBObjects;
    }
}
