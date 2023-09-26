package it.Services.TEST;

import it.Entities.TEST.TestEntity;
import org.bson.types.ObjectId;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public interface Test_Service {
    TestEntity getOne (ObjectId id);
    TestEntity post (TestEntity testEntity);
    ResponseEntity<HttpStatus> delete (ObjectId id);
    Boolean exists (ObjectId id);
}
