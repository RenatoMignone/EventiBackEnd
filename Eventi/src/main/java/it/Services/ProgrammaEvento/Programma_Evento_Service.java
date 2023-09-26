package it.Services.ProgrammaEvento;

import it.Entities.ProgrammaEvento.DTOs.ProgrammaEvento_DTO;
import org.springframework.http.ResponseEntity;

public interface Programma_Evento_Service {
    //POST
    ResponseEntity<?> creaEvento(ProgrammaEvento_DTO prog);

    //GET
    ResponseEntity<?> getProgramma(String id);
    ResponseEntity<?> getProgrammiEventoDaIdEvento(String id_evento);

    //PUT
    ResponseEntity<?> modificaProgramma(String id,ProgrammaEvento_DTO programma);

    //DELETE
    ResponseEntity<?> deleteProgramma(String id);
}
