package it.Services.Biglietto;

import com.google.zxing.WriterException;
import it.Entities.Biglietto.BigliettoEntity;
import it.Entities.Biglietto.DTOs.CreazioneBiglietto_DTO;
import it.Entities.Biglietto.DTOs.ModificaBiglietto_DTO;
import it.Entities.Biglietto.DTOs.Prenotazione_DTO;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.util.List;

public interface Biglietto_Service {
    //POST
    ResponseEntity<?> creaBiglietti(CreazioneBiglietto_DTO creazioneBigliettoDTO);
    ResponseEntity<?> creaBigliettoSingolo(CreazioneBiglietto_DTO creazioneBigliettoDTO);
    ResponseEntity<?> prenotaBiglietto(Prenotazione_DTO prenotazioneDto) throws IOException, WriterException;

    //GET
    ResponseEntity<?> getBiglietto(String id);
    ResponseEntity<?> getNumeroBigliettiDisponibili(String id_evento);
    ResponseEntity<?> getIdBigliettiDaIdEvento(String id_evento, Boolean disponibile);  //Classe wrapper perché nullable
    ResponseEntity<?> getBigliettiDaIdUtente(String id_utente);
    ResponseEntity<?> getAll();

    //PUT
    ResponseEntity<?> modificaBiglietto(ModificaBiglietto_DTO biglietto, String id);
    ResponseEntity<?> modificaBigliettiEvento(ModificaBiglietto_DTO biglietto, String id_evento);

    //DELETE
    ResponseEntity<?> deleteBiglietto(String id);
    ResponseEntity<?> deleteBigliettiByIdEvento(String id_evento);
    ResponseEntity<?> deletePrenotazione(String id);

    //Utilities
    List<String> getIdBigliettiDaLista(List<BigliettoEntity> biglietti);
    int getNumeroBigliettoMax(String id_evento);
}
