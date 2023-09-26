package it.Repositories.db.Eventi;

import it.Entities.ProgrammaEvento.ProgrammaEvento;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface Programma_Evento_Repository extends MongoRepository<ProgrammaEvento,String> {
    ProgrammaEvento findProgrammaEventoById(String id);

    List<ProgrammaEvento> findProgrammaEventosByIdEvento(String id_evento);

    void deleteByIdEvento(String id_evento);
}
