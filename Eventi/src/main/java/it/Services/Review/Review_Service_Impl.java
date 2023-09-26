package it.Services.Review;


import it.Entities.Biglietto.BigliettoEntity;
import it.Entities.Evento.EventoEntity;
import it.Entities.Review.DTOs.Create_Review_DTO;
import it.Entities.Review.DTOs.Modify_Review_DTO;
import it.Entities.Review.ReviewEntity;
import it.Entities.Utente.UserEntity;
import it.Repositories.db.Eventi.Biglietto_Repository;
import it.Repositories.db.Eventi.Evento_Repository;
import it.Repositories.db.Eventi.Review_Repository;
import it.Repositories.db.Utente.User_Repository;
import it.Services.Utente.User_Service_Impl;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class Review_Service_Impl implements Review_Service {
    private final Review_Repository review_repository;
    private final User_Repository user_repository;
    private final Evento_Repository evento_repository;
    private final Biglietto_Repository biglietto_repository;

    @Autowired
    public Review_Service_Impl(Review_Repository review_repository, User_Repository user_repository, Evento_Repository eventoRepository, Biglietto_Repository bigliettoRepository) {
        this.review_repository = review_repository;
        this.user_repository = user_repository;
        evento_repository = eventoRepository;
        biglietto_repository = bigliettoRepository;
    }

//----------------------------------------------------------------------------------------------------------------------
    //POST
    @Override
    public ResponseEntity<?> createReview(Create_Review_DTO reviewDto) {
        //Controllo esistenza utente
        UserEntity user = user_repository.findUserEntityById(reviewDto.getIdUtente());
        if (user == null)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User does not exist");

        //Control on authentication
        if (!User_Service_Impl.getAuthority2(user.getEmail()))
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not authorized");

        //Controllo esistenza evento
        EventoEntity evento = evento_repository.findEventoEntityById(reviewDto.getIdEvento());
        if (evento == null)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Event not found");

        //Controllo se l'utente che scrive la recensione ha partecipato all'evento
        boolean utenteHaBigliettoEvento = false;
        List<BigliettoEntity> bigliettiOccupati = biglietto_repository.findBigliettoEntitiesByIdEventAndIdUtenteIsNotNull(evento.getId());
        for (BigliettoEntity biglietto : bigliettiOccupati)
            if (biglietto.getIdUtente().equals(reviewDto.getIdUtente())) {
                utenteHaBigliettoEvento = true;
                break;
            }
        if (!utenteHaBigliettoEvento)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User must have booked a ticket to write a review");

        //Max una review per utente sullo stesso evento
        if (review_repository.existsReviewEntityByIdEventoAndIdUtente(reviewDto.getIdEvento(), reviewDto.getIdUtente()))
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("There is already a review on this event by this user");


        //Creazione review
        ReviewEntity review = new ReviewEntity();
        review.setAll(reviewDto);
        review.setNomeCognome(user.getNome() + " " + user.getCognome());

        review = review_repository.save(review);
        evento.getIdRecensioni().add(review.getId());
        evento_repository.save(evento);

        return ResponseEntity.status(HttpStatus.CREATED).body(review);
    }

//----------------------------------------------------------------------------------------------------------------------
    //GET
    @Override
    public ReviewEntity getReviewById(String id) {
        return review_repository.findReviewEntityById(id);
    }

    @Override
    public List<String> getReviewsIdByIdEvento(String idEvento) {
        List<ReviewEntity> list = review_repository.findReviewEntitiesByIdEvento(idEvento);
        List<String> ids = new ArrayList<>();

        for (ReviewEntity review : list)
            ids.add(review.getId());

        return ids;
    }

    @Override
    public ResponseEntity<?> getReviewsIdByIdUtente(String idUtente) {
        if (!ObjectId.isValid(idUtente))
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Not an ObjectId");

        UserEntity user = user_repository.findUserEntityById(idUtente);
        if (user == null)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found, try with a different ID");

        //Control on authentication
        if (!User_Service_Impl.getAuthority2(user.getEmail()))
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not authorized");

        List<ReviewEntity> list = review_repository.findReviewEntitiesByIdUtente(idUtente);
        List<String> ids = new ArrayList<>();

        for (ReviewEntity review : list)
            ids.add(review.getId());

        return ResponseEntity.status(HttpStatus.OK).body(ids);
    }

    @Override
    public ResponseEntity<?> getAll() {
        return ResponseEntity.status(HttpStatus.OK).body(review_repository.findAll());
    }

//----------------------------------------------------------------------------------------------------------------------
    //PUT
    @Override
    public ResponseEntity<?> modifyReview(Modify_Review_DTO reviewDto, String id) {
        if (!ObjectId.isValid(id))
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Not an ObjectId");

        ReviewEntity review = review_repository.findReviewEntityById(id);
        if (review == null)
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        //Control on authentication
        UserEntity user = user_repository.findUserEntityById(review.getIdUtente());
        if (!User_Service_Impl.getAuthority2(user.getEmail()))
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not authorized");

        review.setAllMod(reviewDto);
        review_repository.save(review);
        return ResponseEntity.status(HttpStatus.OK).body(review);
    }

//----------------------------------------------------------------------------------------------------------------------
    //DELETE
    @Override
    public ResponseEntity<?> deleteReview(String id) {
        if (!ObjectId.isValid(id))
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Not an ObjectId");

        ReviewEntity review = review_repository.findReviewEntityById(id);
        if (review == null)
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        //Control on authentication
        UserEntity user = user_repository.findUserEntityById(review.getIdUtente());
        if (!User_Service_Impl.getAuthority2(user.getEmail()))
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not authorized");

        //Rimuovi riferimento da evento
        EventoEntity evento = evento_repository.findEventoEntityById(review.getIdEvento());
        evento.getIdRecensioni().remove(id);
        evento_repository.save(evento);

        review_repository.deleteReviewEntityById(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
