package it.Controllers.ProgrammaEvento;

import it.Entities.ProgrammaEvento.DTOs.ProgrammaEvento_DTO;
import it.Services.ProgrammaEvento.Programma_Evento_Service_impl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = {"http://localhost:4200","http://localhost:9878","http://172.31.6.2:4200"})
@RequestMapping("/api/v1")
public class ProgrammaEventoRestController {
    private final Programma_Evento_Service_impl service;
    public ProgrammaEventoRestController(Programma_Evento_Service_impl service) {
        this.service = service;
    }

//-------------------------------------------------------------------------------------------------------------------------------------------
    //POST

    @PostMapping("/programma")
    public ResponseEntity<?> creaProgramma(@RequestBody ProgrammaEvento_DTO dto){
        return service.creaEvento(dto);
    }
//-------------------------------------------------------------------------------------------------------------------------------------------
    //GET

    @GetMapping("/programma/{id}")
    public ResponseEntity<?> getProgramma(@PathVariable String id){
        return service.getProgramma(id);
    }

    @GetMapping("/programma/evento/{id_evento}")
    public ResponseEntity<?> getProgrammaEvento(@PathVariable String id_evento){
        return service.getProgrammiEventoDaIdEvento(id_evento);
    }
//-------------------------------------------------------------------------------------------------------------------------------------------
    //PUT

    @PutMapping("/programma/{id}")
    public ResponseEntity<?> modificaProgramma(@PathVariable String id,@RequestBody ProgrammaEvento_DTO dto){
        return service.modificaProgramma(id, dto);
    }
//-------------------------------------------------------------------------------------------------------------------------------------------
    //DELETE

    @DeleteMapping("/programma/{id}")
    public ResponseEntity<?> deleteProgramma(@PathVariable String id){
    return service.deleteProgramma(id);
}
}
