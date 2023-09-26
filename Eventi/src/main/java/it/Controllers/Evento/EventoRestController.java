package it.Controllers.Evento;

import it.Entities.Evento.DTOs.CreaEvento_DTO;
import it.Entities.Evento.DTOs.ModificaEvento_DTO;
import it.Services.Evento.Evento_Service_Impl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = {"http://localhost:4200","http://localhost:9878","http://172.31.6.2:4200"})
@RequestMapping("/api/v1/evento")
public class EventoRestController {
    private final Evento_Service_Impl service;
    @Autowired
    public EventoRestController(Evento_Service_Impl service) {
        this.service = service;
    }

//-------------------------------------------------------------------------------------------------------------------------------------------
    //POST

    @PostMapping()
    ResponseEntity<?> creaEvento(@RequestBody CreaEvento_DTO dto) {
        return service.creaEvento(dto);
    }

//-------------------------------------------------------------------------------------------------------------------------------------------
    //GET

    @GetMapping("search")
    ResponseEntity<?> searchEventiByNome(@RequestParam("query") String query) {
        return service.searchEventiByNome(query);
    }

    @GetMapping("{id_evento}")
    ResponseEntity<?> getEventoById(@PathVariable String id_evento) {
        return service.getEventoById(id_evento);
    }

    @GetMapping("utente/{id_creatore}")
    public ResponseEntity<?> getEventiDaIdCreatore(@PathVariable String id_creatore) {
        return service.getEventiByIdCreatore(id_creatore);
    }

    @GetMapping("{id_evento}/parcheggi")
    public ResponseEntity<?> getParcheggiAssociatiAEvento(@PathVariable String id_evento) {
        return service.getParcheggiAssociatiAEvento(id_evento);
    }

    @GetMapping("getAll")
    public ResponseEntity<?> getAll() {
        return service.getAll();
    }

//-------------------------------------------------------------------------------------------------------------------------------------------
    //PUT

    @PutMapping("{id_evento}")
    ResponseEntity<?> modificaEvento(@PathVariable String id_evento, @RequestBody ModificaEvento_DTO dto) {
        return service.modificaEvento(id_evento, dto);
    }

//-------------------------------------------------------------------------------------------------------------------------------------------
    //DELETE

    @DeleteMapping("{id_evento}")
    ResponseEntity<?> cancellaEvento(@PathVariable String id_evento) {
        return service.cancellaEvento(id_evento);
    }
}
