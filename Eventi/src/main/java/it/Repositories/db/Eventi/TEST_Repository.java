package it.Repositories.db.Eventi;

import it.Entities.TEST.TestEntity;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TEST_Repository extends MongoRepository<TestEntity,String> {
    TestEntity findById(ObjectId id);
    void deleteById(ObjectId id);

    Boolean existsById (ObjectId id);
}
