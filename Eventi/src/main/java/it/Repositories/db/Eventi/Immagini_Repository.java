package it.Repositories.db.Eventi;

import it.Entities.Immagine.ImmagineEntity;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface Immagini_Repository extends MongoRepository<ImmagineEntity,Integer> {
    ImmagineEntity findImmagineEntityByNome(String nome);
    ImmagineEntity findById(String id);
    List<ImmagineEntity> findImmagineEntitiesByIdevento(String id);
    Boolean existsImmagineEntityById(String id);
    void deleteImmagineEntityById(String id);
}
