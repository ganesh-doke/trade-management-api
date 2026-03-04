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

    /**
     * Convert the object to BasicDBObject using MappingMongoConverter.
     * @param object Object to be converted to BasicDBObject.
     * @return BasicDBObject converted from the object.
     */
    public BasicDBObject getBasicDBObject(Object object) {

        BasicDBObject basicDBObject = new BasicDBObject();
        mappingMongoConverter.write(object, basicDBObject);
        return basicDBObject;
    }

    /**
     * Convert the list of objects to list of BasicDBObject using MappingMongoConverter.
     * @param list List of objects to be converted to list of BasicDBObject.
     * @return List of BasicDBObject converted from the list of objects.
     */
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
