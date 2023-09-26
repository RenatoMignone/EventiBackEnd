package it.Services.Review;


import it.Entities.Review.DTOs.Create_Review_DTO;
import it.Entities.Review.DTOs.Modify_Review_DTO;
import it.Entities.Review.ReviewEntity;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface Review_Service {
    //POST
    ResponseEntity<?> createReview(Create_Review_DTO reviewDto);

    //GET methods
    ReviewEntity getReviewById(String id);
    List<String> getReviewsIdByIdEvento(String idEvento);
    ResponseEntity<?> getReviewsIdByIdUtente(String idUtente);
    ResponseEntity<?> getAll();

    //PUT
    ResponseEntity<?> modifyReview(Modify_Review_DTO reviewDto, String id);

    //DELETE
    ResponseEntity<?> deleteReview(String id);
}
