package it.Services.Evento;

import it.Entities.Evento.DTOs.CreaEvento_DTO;
import it.Entities.Evento.DTOs.ModificaEvento_DTO;
import org.springframework.http.ResponseEntity;

public interface Evento_Service {
    //POST
    ResponseEntity<?> creaEvento(CreaEvento_DTO dto);

    //GET
    ResponseEntity<?> searchEventiByNome(String query);
    ResponseEntity<?> getEventoById(String id);
    ResponseEntity<?> getEventiByIdCreatore(String id_creatore);
    ResponseEntity<?> getParcheggiAssociatiAEvento(String id_evento);
    ResponseEntity<?> getAll();

    //PUT
    ResponseEntity<?> modificaEvento(String id, ModificaEvento_DTO dto);

    //DELETE
    ResponseEntity<?> cancellaEvento(String id);
}
