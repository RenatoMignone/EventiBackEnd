package it.Services.TEST;

import it.Entities.TEST.TestEntity;
import it.Repositories.db.Eventi.TEST_Repository;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class Test_Service_impl implements Test_Service{
    private final TEST_Repository repo;
    @Autowired
    public Test_Service_impl(TEST_Repository repo) {
        this.repo = repo;
    }


    @Override
    public TestEntity getOne(ObjectId id) {
        return repo.findById(id);
    }

    @Override
    public TestEntity post(TestEntity testEntity) {
        return repo.save(testEntity);
    }

    @Override
    public ResponseEntity<HttpStatus> delete(ObjectId id) {
        repo.deleteById(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Override
    public Boolean exists(ObjectId id) {
        return repo.existsById(id);
    }


}
