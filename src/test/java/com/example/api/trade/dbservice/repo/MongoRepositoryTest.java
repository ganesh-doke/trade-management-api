package com.example.api.trade.dbservice.repo;

import com.mongodb.BasicDBObject;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(SpringExtension.class)
class MongoRepositoryTest {

    @Mock private MappingMongoConverter mappingMongoConverter;
    @InjectMocks private TestMongoRepository repository;
    private static class TestMongoRepository extends MongoRepository {}

    @Test
    void testGetBasicDBObject() {

        TestObject testObject1 = new TestObject("test1");
        TestObject testObject2 = new TestObject("test1");
        List<TestObject> testObjects = Arrays.asList(testObject1, testObject2);

        BasicDBObject basicDBObject = repository.getBasicDBObject(testObject1);
        Assertions.assertNotNull(basicDBObject);

        List<BasicDBObject> basicDBObjectList = repository.getBasicDbObjectList(testObjects);
        Assertions.assertNotNull(basicDBObjectList);
        Assertions.assertEquals(2, basicDBObjectList.size());
        verify(mappingMongoConverter, times(3)).write(any(), any(BasicDBObject.class));

        basicDBObjectList = repository.getBasicDbObjectList(new ArrayList<>());
        Assertions.assertNull(basicDBObjectList);
    }

    @AllArgsConstructor
    @Getter
    private static class TestObject {
        private String value;
    }
}
