package it.Repositories.db.Eventi;

import it.Entities.Review.ReviewEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface Review_Repository extends MongoRepository<ReviewEntity,String> {
    Boolean existsReviewEntityById(String id);
    Boolean existsReviewEntityByIdEventoAndIdUtente(String eventId, String userId);

    ReviewEntity findReviewEntityById(String id);

    List<ReviewEntity> findReviewEntitiesByIdEvento(String eventId);
    List<ReviewEntity> findReviewEntitiesByIdUtente(String userId);

    void deleteReviewEntityById(String id);
    void deleteByIdEvento(String id_evento);
}
