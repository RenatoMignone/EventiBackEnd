package it.Controllers.Biglietto;

import it.Entities.Biglietto.DTOs.CreazioneBiglietto_DTO;
import it.Entities.Biglietto.DTOs.ModificaBiglietto_DTO;
import it.Entities.Biglietto.DTOs.Prenotazione_DTO;
import it.Services.Biglietto.Biglietto_Service_impl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = {"http://localhost:4200","http://localhost:9878","http://172.31.6.2:4200"})
@RestController
@RequestMapping("/api/v1/biglietto")
public class BigliettoRestController {
    private final Biglietto_Service_impl service;

    @Autowired
    public BigliettoRestController(Biglietto_Service_impl service) {
        this.service = service;
    }

//-------------------------------------------------------------------------------------------------------------------------------------------
    //POST
    @PostMapping("crea")
    public ResponseEntity<?> creaBiglietti(@RequestBody CreazioneBiglietto_DTO creazioneBigliettoDTO) {
        return service.creaBiglietti(creazioneBigliettoDTO);
    }


    @PostMapping()
    public ResponseEntity<?> creaBigliettoSingolo(@RequestBody CreazioneBiglietto_DTO creazioneBigliettoDTO) {
        return service.creaBigliettoSingolo(creazioneBigliettoDTO);
    }

    @PostMapping("prenota")
    public ResponseEntity<?> prenotazioneBiglietto(@RequestBody Prenotazione_DTO prenotazioneDto) {
        return service.prenotaBiglietto(prenotazioneDto);
    }

//-------------------------------------------------------------------------------------------------------------------------------------------
    //GET
    @GetMapping("{id_biglietto}")
    public ResponseEntity<?> getBiglietto(@PathVariable String id_biglietto){
        return service.getBiglietto(id_biglietto);
    }

    @GetMapping("disponibili/{id_evento}")
    public ResponseEntity<?> getNumeroBigliettiDisponibili(@PathVariable String id_evento){
        return service.getNumeroBigliettiDisponibili(id_evento);
    }

    @GetMapping("evento/{id_evento}")
    public ResponseEntity<?> getIdBigliettiDaEvento(@PathVariable String id_evento,
                                                    @RequestParam(value = "disponibile", required = false) Boolean disponibile) {
        return service.getIdBigliettiDaIdEvento(id_evento, disponibile);
    }

    @GetMapping("utente/{id_utente}")
    public ResponseEntity<?> getBigliettiDaIdUtente(@PathVariable String id_utente) {
        return service.getBigliettiDaIdUtente(id_utente);
    }

    @GetMapping("getAll")
    public ResponseEntity<?> getAll() {
        return service.getAll();
    }

//-------------------------------------------------------------------------------------------------------------------------------------------
    //PUT
    @PutMapping("{id_biglietto}")
    public ResponseEntity<?> modificaBiglietto(@RequestBody ModificaBiglietto_DTO biglietto, @PathVariable String id_biglietto){
        return service.modificaBiglietto(biglietto, id_biglietto);
    }

    @PutMapping("evento/{id_evento}")
    public ResponseEntity<?> modificaBigliettiEvento(@RequestBody ModificaBiglietto_DTO biglietto, @PathVariable String id_evento){
        return service.modificaBigliettiEvento(biglietto, id_evento);
    }

//-------------------------------------------------------------------------------------------------------------------------------------------
    //DELETE
    @DeleteMapping("{id_biglietto}")
    public ResponseEntity<?> deleteBiglietto(@PathVariable String id_biglietto){
        return service.deleteBiglietto(id_biglietto);
    }

    @DeleteMapping("evento/{id_evento}")
    public ResponseEntity<?> deleteBigliettiByIdEvento(@PathVariable String id_evento){
        return service.deleteBigliettiByIdEvento(id_evento);
    }

    @DeleteMapping("prenota/{id_biglietto}")
    public ResponseEntity<?> deletePrenotazione(@PathVariable String id_biglietto){
        return service.deletePrenotazione(id_biglietto);
    }
}
