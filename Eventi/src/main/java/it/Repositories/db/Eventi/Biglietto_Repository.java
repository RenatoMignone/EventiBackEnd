package it.Repositories.db.Eventi;

import it.Entities.Biglietto.BigliettoEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface Biglietto_Repository extends MongoRepository<BigliettoEntity, String> {
    BigliettoEntity findBigliettoEntityById(String id);

    List<BigliettoEntity> findBigliettoEntitiesByIdEvent(String id_evento);
    List<BigliettoEntity> findBigliettoEntitiesByIdEventAndIdUtenteIsNull(String id_evento);
    List<BigliettoEntity> findBigliettoEntitiesByIdEventAndIdUtenteIsNotNull(String id_evento);
    List<BigliettoEntity> findBigliettoEntitiesByIdUtente(String id_utente);

    void deleteByIdEvent(String id_evento);
}
