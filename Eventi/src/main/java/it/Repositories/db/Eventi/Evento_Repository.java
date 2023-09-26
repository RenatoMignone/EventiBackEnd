package it.Repositories.db.Eventi;

import it.Entities.Evento.EventoEntity;
import it.Entities.Position.Position;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Date;
import java.util.List;

public interface Evento_Repository extends MongoRepository<EventoEntity, String> {
    Boolean existsEventoEntityByNomeAndDataInizioAndDataFineAndLocation(String nome, Date dataInizio, Date dataFine, Position location);

    EventoEntity findEventoEntityById(String id);
    List<EventoEntity> findEventoEntitiesByIdCreatore(String id_creatore);
    List<EventoEntity> findEventoEntitiesByNomeContainingIgnoreCase(String query);

    void deleteEventoEntityById(String id);
}
