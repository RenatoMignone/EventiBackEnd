package it.Controllers.Review;


import it.Entities.Review.DTOs.Create_Review_DTO;
import it.Entities.Review.DTOs.Modify_Review_DTO;
import it.Entities.Review.ReviewEntity;
import it.Repositories.db.Eventi.Evento_Repository;
import it.Services.Review.Review_Service_Impl;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import lombok.extern.java.Log;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.beanvalidation.SpringValidatorAdapter;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Log
@RestController
@RequestMapping("api/v1/" + "recensione")
@CrossOrigin(origins = {"http://localhost:4200","http://localhost:9878","http://172.31.6.2:4200"})
public class ReviewController {
    private final Review_Service_Impl service;

    private final Evento_Repository evento_repository;

    private final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    private final Validator validator = factory.getValidator();

    @Autowired
    public ReviewController(Review_Service_Impl service, Evento_Repository eventoRepository) {
        this.service = service;
        evento_repository = eventoRepository;
    }

//-------------------------------------------------------------------------------------------------------------------------------------------
    //POST
    @PostMapping()
    public ResponseEntity<?> create(@RequestBody Create_Review_DTO reviewDto) {
        SpringValidatorAdapter v = new SpringValidatorAdapter(validator);
        BeanPropertyBindingResult errors = new BeanPropertyBindingResult(reviewDto, "reviewDto");

        v.validate(reviewDto, errors);
        if (errors.hasErrors())
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Missing mandatory fields");

        return service.createReview(reviewDto);
    }

//-------------------------------------------------------------------------------------------------------------------------------------------
    //GET
    @GetMapping("{idRecensione}")
    public ResponseEntity<?> getReviewById(@PathVariable String idRecensione){
        if (!ObjectId.isValid(idRecensione))
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        ReviewEntity review = service.getReviewById(idRecensione);

        if(review == null)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Review not found. Try again with another ID");

        return ResponseEntity.status(HttpStatus.OK).body(review);
    }

    @GetMapping("evento/{idEvento}")
    public ResponseEntity<?> getReviewsByIdEvent(@PathVariable String idEvento) {
        if (!ObjectId.isValid(idEvento))
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        if (!evento_repository.existsById(idEvento))
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Event not found");

        List<String> ids = service.getReviewsIdByIdEvento(idEvento);
        if(ids == null)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("There are no reviews for this event");

        return ResponseEntity.status(HttpStatus.OK).body(ids);
    }

    @GetMapping("utente/{idUtente}")
    public ResponseEntity<?> getReviewsByUserId(@PathVariable String idUtente) {
        if (!ObjectId.isValid(idUtente))
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        ResponseEntity<?> response = service.getReviewsIdByIdUtente(idUtente);

        if (response.getStatusCode().equals(HttpStatus.OK) && response.getBody() == null)
            return new ResponseEntity<>(HttpStatus.OK);

        return response;
    }

    @GetMapping("getAll")
    public ResponseEntity<?> getAll() {
        return service.getAll();
    }

//-------------------------------------------------------------------------------------------------------------------------------------------
    //PUT
    @PutMapping("{idRecensione}")
    public ResponseEntity<?> modify(@RequestBody Modify_Review_DTO reviewDto, @PathVariable String idRecensione) {
        if (!ObjectId.isValid(idRecensione))
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        SpringValidatorAdapter v = new SpringValidatorAdapter(validator);
        BeanPropertyBindingResult errors = new BeanPropertyBindingResult(reviewDto, "reviewDto");

        v.validate(reviewDto, errors);
        if (errors.hasErrors())
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        return service.modifyReview(reviewDto, idRecensione);
    }

//-------------------------------------------------------------------------------------------------------------------------------------------
    //DELETE
    @DeleteMapping("{idRecensione}")
    public ResponseEntity<?> delete(@PathVariable String idRecensione) {
        return service.deleteReview(idRecensione);
    }
}
