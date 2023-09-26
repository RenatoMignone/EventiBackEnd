package it.Controllers.TEST;

import it.Entities.TEST.DTOs.TestEntityDTO;
import it.Entities.TEST.TestEntity;
import it.Services.TEST.Test_Service_impl;
import lombok.extern.java.Log;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = {"http://localhost:4200","http://localhost:9878","http://172.31.6.2:4200"})
@RestController
@Log
@RequestMapping("/api/v1/test/")
public class TestController {
    private final Test_Service_impl service;
    @Autowired
    public TestController(Test_Service_impl service) {
        this.service = service;
    }

    @GetMapping("/{id}")
    public TestEntity get(@PathVariable ObjectId id)
    {
        return service.getOne(id);
    }

    @PostMapping
    public ResponseEntity<HttpStatus> post(@RequestBody TestEntityDTO entity){
        log.info(entity.getStringa());
        TestEntity test = new TestEntity();
        test.setAll(entity);
        service.post(test);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> delete(@PathVariable ObjectId id){
        if(service.exists(id)){
            return service.delete(id);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }
}
