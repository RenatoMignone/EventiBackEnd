package it.Services.Biglietto;

import it.BasicServices.Pasquale.Mail.GmailEmailSender;
import it.Entities.Biglietto.BigliettoEntity;
import it.Entities.Biglietto.DTOs.CreazioneBiglietto_DTO;
import it.Entities.Biglietto.DTOs.ModificaBiglietto_DTO;
import it.Entities.Biglietto.DTOs.Prenotazione_DTO;
import it.Entities.Evento.EventoEntity;
import it.Entities.Utente.UserEntity;
import it.Repositories.db.Eventi.Biglietto_Repository;
import it.Repositories.db.Eventi.Evento_Repository;
import it.Repositories.db.Utente.User_Repository;
import it.Services.Utente.User_Service_Impl;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.beanvalidation.SpringValidatorAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
public class Biglietto_Service_impl implements Biglietto_Service{
    private final Biglietto_Repository biglietto_repository;
    private final User_Repository user_repository;
    private final Evento_Repository evento_repository;

    private final GmailEmailSender emailSender;

    private final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    private final Validator validator = factory.getValidator();

    @Autowired
    public Biglietto_Service_impl(Biglietto_Repository bigliettoRepository, User_Repository userRepository, Evento_Repository eventoRepository, GmailEmailSender emailSender) {
        biglietto_repository = bigliettoRepository;
        user_repository = userRepository;
        evento_repository = eventoRepository;
        this.emailSender = emailSender;
    }

//-------------------------------------------------------------------------------------------------------------------------------------------
    //POST

    /**
     * Metodo per creare vari biglietti associati a un evento. Solo il proprietario dell'evento dovrebbe poter
     * eseguire questo metodo. Specifiche sul numero di biglietti da creare all'interno del DTO.
     * AUTH: Creatore evento o admin
     *
     * @param creazioneBigliettoDTO Contiene ID evento associato e numero di biglietti da creare
     * @return Errore su creazione dei biglietti (bad request), errore su autenticazione oppure CREATED + oggetti
     */
    @Override
    public ResponseEntity<?> creaBiglietti(CreazioneBiglietto_DTO creazioneBigliettoDTO) {
    //Controlli
        //Validation
        SpringValidatorAdapter v = new SpringValidatorAdapter(validator);
        BeanPropertyBindingResult errors = new BeanPropertyBindingResult(creazioneBigliettoDTO, "creazioneBiglietto");

        v.validate(creazioneBigliettoDTO, errors);
        if (errors.hasErrors())
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Missing mandatory fields");

        //Controllo esistenza evento
        EventoEntity evento = evento_repository.findEventoEntityById(creazioneBigliettoDTO.getIdEvento());
        if (evento == null)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Event not found");

        //Controllo su autenticazione
        UserEntity user = user_repository.findUserEntityById(evento.getIdCreatore());
        if (!User_Service_Impl.getAuthority2(user.getEmail()))
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not authorized");

        //Data biglietto compresa tra data inizio e data fine evento
        if (!(creazioneBigliettoDTO.getData().compareTo(evento.getDataInizio()) >= 0
                && creazioneBigliettoDTO.getData().compareTo(evento.getDataFine()) <= 0) )
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Ticket date must be between the event's dates");


    //Creazione - Tramite l'offset continuiamo in ordine i numeri seriali senza farli sovrapporre
        int offset = this.getNumeroBigliettoMax(creazioneBigliettoDTO.getIdEvento());
        List<BigliettoEntity> biglietti = new ArrayList<>();
        for (int i = 1; i <= creazioneBigliettoDTO.getNumeroBiglietti(); i++) {
            BigliettoEntity biglietto = new BigliettoEntity();
            biglietto.setAll(creazioneBigliettoDTO);
            biglietto.setNBiglietto(i + offset);
            biglietto.setLocation(evento.getLocation());

            biglietto = biglietto_repository.save(biglietto);
            biglietti.add(biglietto);
            evento.getIdBiglietti().add(biglietto.getId());
        }
        evento_repository.save(evento);

        return ResponseEntity.status(HttpStatus.CREATED).body(biglietti);
    }

    /**
     * Crea un singolo biglietto. Controllo su se il numero del biglietto è già stato preso.
     *
     * @param creazioneBigliettoDTO Informazioni sul biglietto da creare
     * @return CREATED + biglietto appena creato, oppure ERRORE
     */
    @Override
    public ResponseEntity<?> creaBigliettoSingolo(CreazioneBiglietto_DTO creazioneBigliettoDTO) {
    //Controlli
        //Validation
        SpringValidatorAdapter v = new SpringValidatorAdapter(validator);
        BeanPropertyBindingResult errors = new BeanPropertyBindingResult(creazioneBigliettoDTO, "creazioneBiglietto");

        v.validate(creazioneBigliettoDTO, errors);
        if (errors.hasErrors())
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Missing mandatory fields");

        //Controllo numero seriale
        if (checkIfSerialNumberIsAlreadyTaken(creazioneBigliettoDTO.getIdEvento(), creazioneBigliettoDTO.getNumeroBiglietti()))
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Ticket number already taken");

        //Controllo esistenza evento
        EventoEntity evento = evento_repository.findEventoEntityById(creazioneBigliettoDTO.getIdEvento());
        if (evento == null)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Event not found");

        //Controllo su autenticazione
        UserEntity user = user_repository.findUserEntityById(evento.getIdCreatore());
        if (!User_Service_Impl.getAuthority2(user.getEmail()))
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not authorized");

        //Data biglietto compresa tra data inizio e data fine evento
        if (!(creazioneBigliettoDTO.getData().compareTo(evento.getDataInizio()) >= 0
                && creazioneBigliettoDTO.getData().compareTo(evento.getDataFine()) <= 0) )
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Ticket date must be between the event's dates");


    //Creazione
        BigliettoEntity biglietto = new BigliettoEntity();
        biglietto.setAll(creazioneBigliettoDTO);
        biglietto.setLocation(evento.getLocation());

        biglietto = biglietto_repository.save(biglietto);
        evento.getIdBiglietti().add(biglietto.getId());
        evento_repository.save(evento);

        return ResponseEntity.status(HttpStatus.CREATED).body(biglietto);
    }

    /**
     * Metodo per prenotare un biglietto. L'utente deve essere autenticato e l'ID utente deve corrispondere.
     * Quando si prenota, viene mandata un'email con il codice QR del biglietto.
     * AUTH: Utenti autenticati
     *
     * @param prenotazioneDto Contiene ID utente e ID biglietto da prenotare
     * @return Errore BAD_REQUEST se i parametri non sono ObjectId, NOT_FOUND se non troviamo utente o biglietto,
     * altrimenti OK + biglietto
     */
    @Override
    public ResponseEntity<?> prenotaBiglietto(Prenotazione_DTO prenotazioneDto) {
        //Validation
        SpringValidatorAdapter v = new SpringValidatorAdapter(validator);
        BeanPropertyBindingResult errors = new BeanPropertyBindingResult(prenotazioneDto, "prenotazioneDto");

        v.validate(prenotazioneDto, errors);
        if (errors.hasErrors())
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Missing mandatory fields");

        //Controlli su validità ID ed esistenza istanze
        if (!ObjectId.isValid(prenotazioneDto.getIdUtente()) || !ObjectId.isValid(prenotazioneDto.getIdBiglietto()))
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Not an ObjectId");

        if(!user_repository.existsById(prenotazioneDto.getIdUtente()))
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");

        if (!biglietto_repository.existsById(prenotazioneDto.getIdBiglietto()))
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Ticket not found");

        //Controllo su autenticazione
        UserEntity user = user_repository.findUserEntityById(prenotazioneDto.getIdUtente());
        if (!User_Service_Impl.getAuthority2(user.getEmail()))
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not authorized");

        //Controllo precedente prenotazione biglietto
        BigliettoEntity biglietto = biglietto_repository.findBigliettoEntityById(prenotazioneDto.getIdBiglietto());
        if (biglietto.getIdUtente() != null)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Ticket already booked");


        //Prenotazione
        biglietto.setIdUtente(prenotazioneDto.getIdUtente());

        //Generazione codice QR e invio email (da fare)
        emailSender.sendEmailQR(user.getEmail(), "Prenotazione biglietto",
                "Gentile utente " + user.getUsername() + ",\n" +
                        "Con la qui presente siamo lieti di inviarLe il codice QR relativo alla sua prenotazione.\n" +
                        "La ringraziamo per averci scelto.", biglietto);

        biglietto_repository.save(biglietto);
        return ResponseEntity.status(HttpStatus.OK).body(biglietto);
    }
//-------------------------------------------------------------------------------------------------------------------------------------------
    //GET

    /**
     * Data una ID di un biglietto, esegue dei controlli sul path e sull'identità del richiedente, per poi restituire
     * un valore di conseguenza.
     * AUTH: Proprietario del biglietto, creatore evento o admin
     *
     * @param id ID del biglietto da ottenere
     * @return Il biglietto associato all'ID oppure restituisce un ERRORE
     */
    @Override
    public ResponseEntity<?> getBiglietto(String id) {
        if (!ObjectId.isValid(id))
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Not an ObjectId");

        BigliettoEntity biglietto = biglietto_repository.findBigliettoEntityById(id);

        if (biglietto == null)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Ticket not found");

        //Control on authentication
        UserEntity user = user_repository.findUserEntityById(biglietto.getIdUtente());
        if (!User_Service_Impl.getAuthority2(user.getEmail()))
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not authorized");

        return ResponseEntity.status(HttpStatus.OK).body(biglietto);
    }

    /**
     * Data l'ID di un evento, effettua controlli sul path e sulla sua esistenza dell'evento richiesto, ed eventualmente
     * restituire il numero di biglietti disponibili di un evento.
     * AUTH: Chiunque
     *
     * @param id_evento ID dell'evento di cui restituire il numero di biglietti disponibili
     * @return Numero di biglietti disponibili (int) o ERRORE
     */
    @Override
    public ResponseEntity<?> getNumeroBigliettiDisponibili(String id_evento) {
        //Controlli
        if (!ObjectId.isValid(id_evento))
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Not an ObjectId");

        if (!evento_repository.existsById(id_evento))
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Event not found");


        List<BigliettoEntity> biglietti = biglietto_repository.findBigliettoEntitiesByIdEventAndIdUtenteIsNull(id_evento);
        return ResponseEntity.status(HttpStatus.OK).body(biglietti.size());
    }

    /**
     * Data l'ID di un evento, effettua controlli e restituisce la lista delle ID dei suoi relativi biglietti. A seconda
     * del parametro "disponibile", restituisce l'elenco dei biglietti disponibili, quelli già prenotati o tutti.
     * AUTH: Chiunque (???)
     *
     * @param id_evento ID dell'evento di cui restituire la lista delle ID dei biglietti
     * @param disponibile Boolean, può indicare se mostrare i biglietti disponibili o meno
     * @return Lista delle ID (Stringhe) dei biglietti di un evento
     */
    @Override
    public ResponseEntity<?> getIdBigliettiDaIdEvento(String id_evento, Boolean disponibile) {
        //Controlli
        if (!ObjectId.isValid(id_evento))
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Not an ObjectId");

        if (!evento_repository.existsById(id_evento))
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Event not found");


        //If true --> return ids of available tickets
        if (Boolean.TRUE.equals(disponibile))
            return ResponseEntity.status(HttpStatus.OK)
                    .body(this.getIdBigliettiDaLista(biglietto_repository.findBigliettoEntitiesByIdEventAndIdUtenteIsNull(id_evento)));

        //If false --> return ids of not available tickets
        if (Boolean.FALSE.equals(disponibile))
            return ResponseEntity.status(HttpStatus.OK)
                    .body(this.getIdBigliettiDaLista(biglietto_repository.findBigliettoEntitiesByIdEventAndIdUtenteIsNotNull(id_evento)));

        //If null --> return all ids
        return ResponseEntity.status(HttpStatus.OK)
                .body(this.getIdBigliettiDaLista(biglietto_repository.findBigliettoEntitiesByIdEvent(id_evento)));
    }

    @Override
    public ResponseEntity<?> getBigliettiDaIdUtente(String id_utente) {
        if (!ObjectId.isValid(id_utente))
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Not an ObjectId");

        UserEntity user = user_repository.findUserEntityById(id_utente);
        if (user == null)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");

        if (!User_Service_Impl.getAuthority2(user.getEmail()))
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not authorized");

        return ResponseEntity.status(HttpStatus.OK).body(biglietto_repository.findBigliettoEntitiesByIdUtente(id_utente));
    }

    @Override
    public ResponseEntity<?> getAll() {
        return ResponseEntity.status(HttpStatus.OK).body(biglietto_repository.findAll());
    }

//-------------------------------------------------------------------------------------------------------------------------------------------
    //PUT

    /**
     * Metodo per il creatore dell'evento per modificare un biglietto già esistente tramite l'id del biglietto.
     * Notifica anche all'utente l'avvenimento di modifiche avvenute sul suo biglietto, se è presente una idUtente.
     * AUTH: Creatore evento o admin
     *
     * @param biglietto DTO che contiene le modifiche da apportare al biglietto
     * @param id ID del biglietto da modificare
     * @return OK + eventuale email all'utente, oppure ERRORE
     */
    @Override
    public ResponseEntity<?> modificaBiglietto(ModificaBiglietto_DTO biglietto, String id) {
    //Controlli
        //Validazione
        SpringValidatorAdapter v = new SpringValidatorAdapter(validator);
        BeanPropertyBindingResult errors = new BeanPropertyBindingResult(biglietto, "biglietto");

        v.validate(biglietto, errors);
        if (errors.hasErrors())
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Missing mandatory fields");

        //Controllo validità ObjectId
        if (!ObjectId.isValid(id))
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Not an ObjectId");

        //Controllo esistenza biglietto
        BigliettoEntity ticket = biglietto_repository.findBigliettoEntityById(id);
        if (ticket == null)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Ticket not found");

        //Controllo esistenza evento
        EventoEntity evento = evento_repository.findEventoEntityById(ticket.getIdEvent());
        if (evento == null)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Event not found");

        //Controllo su autenticazione
        UserEntity user = user_repository.findUserEntityById(evento.getIdCreatore());
        if (!User_Service_Impl.getAuthority2(user.getEmail()))
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not authorized");

        //Data biglietto compresa tra data inizio e data fine evento
        if (!(biglietto.getData().compareTo(evento.getDataInizio()) >= 0
                && biglietto.getData().compareTo(evento.getDataFine()) <= 0) )
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Ticket date must be between the event's dates");

    //Modifica
        ticket.setMod(biglietto);
        ticket.setLocation(evento.getLocation());

        //Mandare email col nuovo codice QR se c'è un utente
        if (ticket.getIdUtente() != null){
            UserEntity ticketOwner = user_repository.findUserEntityById(ticket.getIdUtente());
            emailSender.sendEmailQR(ticketOwner.getEmail(), "Prenotazione biglietto",
                    "Gentile utente " + user.getUsername() + ",\n" +
                            "Con la qui presente siamo lieti di inviarLe il codice QR relativo alla sua prenotazione.\n" +
                            "La ringraziamo per averci scelto.", ticket);
        }

        biglietto_repository.save(ticket);
        return ResponseEntity.status(HttpStatus.OK).body(ticket);
    }

    @Override
    public ResponseEntity<?> modificaBigliettiEvento(ModificaBiglietto_DTO biglietto, String id_evento) {
    //Controlli
        //Validazione
        SpringValidatorAdapter v = new SpringValidatorAdapter(validator);
        BeanPropertyBindingResult errors = new BeanPropertyBindingResult(biglietto, "biglietto");

        v.validate(biglietto, errors);
        if (errors.hasErrors())
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Missing mandatory fields");

        //Controllo validità ObjectId
        if (!ObjectId.isValid(id_evento))
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Not an ObjectId");

        //Controllo esistenza evento
        EventoEntity evento = evento_repository.findEventoEntityById(id_evento);
        if (evento == null)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Event not found");

        //Controllo su autenticazione
        UserEntity user = user_repository.findUserEntityById(evento.getIdCreatore());
        if (!User_Service_Impl.getAuthority2(user.getEmail()))
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not authorized");

        //Data biglietti compresa tra data inizio e data fine evento
        if (!(biglietto.getData().compareTo(evento.getDataInizio()) >= 0
                && biglietto.getData().compareTo(evento.getDataFine()) <= 0) )
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Ticket date must be between the event's dates");


    //Modifica
        List<BigliettoEntity> biglietti  = biglietto_repository.findBigliettoEntitiesByIdEvent(id_evento);

        //Se un utente era associato già al biglietto, mandare email di avvenuta modifica
        for (BigliettoEntity ticket : biglietti) {
            ticket.setMod(biglietto);
            ticket.setLocation(evento.getLocation());

            if (ticket.getIdUtente() != null) {
                UserEntity ticketOwner = user_repository.findUserEntityById(ticket.getIdUtente());
                emailSender.sendEmailQR(ticketOwner.getEmail(), "Prenotazione biglietto",
                        "Gentile utente " + user.getUsername() + ",\n" +
                                "Con la qui presente siamo lieti di inviarLe il codice QR relativo alla sua prenotazione " +
                                "dopo la modifica.\n" +
                                "La ringraziamo per averci scelto.", ticket);
            }

            biglietto_repository.save(ticket);
        }

        return ResponseEntity.status(HttpStatus.OK).body(biglietti);
    }

//-------------------------------------------------------------------------------------------------------------------------------------------
    //DELETE

    /**
     * Cancella un singolo biglietto data la sua ID, dopo aver fatto i relativi controlli di sicurezza.
     * AUTH: Creatore evento o admin
     *
     * @param id ID del biglietto da cancellare
     * @return OK o ERRORE
     */
    @Override
    public ResponseEntity<?> deleteBiglietto(String id) {
        //Controlli
        if (!ObjectId.isValid(id))
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Not an ObjectId");

        BigliettoEntity biglietto = biglietto_repository.findBigliettoEntityById(id);
        if (biglietto == null)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Ticket not found");

        //Controllo su autenticazione
        EventoEntity evento = evento_repository.findEventoEntityById(biglietto.getIdEvent());
        UserEntity user = user_repository.findUserEntityById(evento.getIdCreatore());
        if (!User_Service_Impl.getAuthority2(user.getEmail()))
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not authorized");


        //Se un utente era associato già al biglietto, mandare email di avvenuta cancellazione
        if (biglietto.getIdUtente() != null){
            UserEntity ticketOwner = user_repository.findUserEntityById(biglietto.getIdUtente());
            emailSender.sendEmail(ticketOwner.getEmail(), "Cancellazione biglietto evento " + evento.getNome(),
                    "Gentile utente " + ticketOwner.getUsername() + ",\n" +
                            "Le scriviamo per informarLa riguardo la cancellazione del biglietto da Lei " +
                            "acquistato effettuata dal creatore dell'evento.\n" +
                            "Ci scusiamo per l'inconveniente.");
        }

        //Rimozione biglietto da evento
        evento.getIdBiglietti().remove(id);
        evento_repository.save(evento);

        biglietto_repository.deleteById(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * Cancella tutti i biglietti di un evento data la sua ID.
     * AUTH: Creatore evento o admin
     *
     * @param id_evento ID dell'evento di cui si vogliono cancellare tutti i biglietti
     * @return OK o ERRORE
     */
    @Override
    public ResponseEntity<?> deleteBigliettiByIdEvento(String id_evento) {
        //Controlli
        if (!ObjectId.isValid(id_evento))
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Not an ObjectId");

        EventoEntity evento = evento_repository.findEventoEntityById(id_evento);
        if (evento == null)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Event not found");

        //Controllo su autenticazione
        UserEntity user = user_repository.findUserEntityById(evento.getIdCreatore());
        if (!User_Service_Impl.getAuthority2(user.getEmail()))
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not authorized");


        //Avviso cancellazione ai proprietari dei biglietti già prenotati
        for (BigliettoEntity biglietto : biglietto_repository.findBigliettoEntitiesByIdEventAndIdUtenteIsNotNull(id_evento)) {
            UserEntity ticketOwner = user_repository.findUserEntityById(biglietto.getIdUtente());
            emailSender.sendEmail(ticketOwner.getEmail(), "Cancellazione biglietto evento " + evento.getNome(),
                    "Gentile utente " + ticketOwner.getUsername() + ",\n" +
                            "Le scriviamo per informarLa riguardo la cancellazione del biglietto da Lei" +
                            "acquistato effettuata dal creatore dell'evento.\n" +
                            "Ci scusiamo per l'inconveniente.");
        }

        //Rimozione biglietti da evento
        Set<String> idBiglietti = evento.getIdBiglietti();
        evento.getIdBiglietti().removeAll(idBiglietti);

        evento_repository.save(evento);
        biglietto_repository.deleteByIdEvent(id_evento);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * Cancella la prenotazione effettuata a un evento, data la sua ID e dopo aver effettuato i controlli di sicurezza.
     * AUTH: Proprietario biglietto, creatore evento o admin
     *
     * @param id ID del biglietto di cui cancellare la prenotazione
     * @return OK o ERRORE
     */
    @Override
    public ResponseEntity<?> deletePrenotazione(String id) {
        if (!ObjectId.isValid(id))
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Not an ObjectId");

        BigliettoEntity biglietto = biglietto_repository.findBigliettoEntityById(id);
        if (biglietto == null)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Ticket not found");

        //Control on authentication
        UserEntity user = user_repository.findUserEntityById(biglietto.getIdUtente());
        if (!User_Service_Impl.getAuthority2(user.getEmail()))
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not authorized");

        biglietto.setIdUtente(null);
        biglietto_repository.save(biglietto);
        return new ResponseEntity<>(HttpStatus.OK);
    }

//-------------------------------------------------------------------------------------------------------------------------------------------
    //Utilities

    /**
     * Data una lista di biglietti, restituisci la lista delle loro ID
     *
     * @param biglietti Lista di BigliettoEntity
     * @return Lista delle ID (stringhe)
     */
    @Override
    public List<String> getIdBigliettiDaLista(List<BigliettoEntity> biglietti) {
        List<String> ids = new ArrayList<>();

        for (BigliettoEntity x : biglietti)
            ids.add(x.getId());

        return ids;
    }

    /**
     * Restituisce l'ultimo numero seriale del biglietto.
     * Estremamente utile per non duplicare il numero seriale del biglietto.
     *
     * @param id_evento ID dell'evento di cui si vuole ottenere l'ultimo numero seriale del biglietto
     * @return (int) offset da sommare al numero seriale dei nuovi biglietti
     */
    @Override
    public int getNumeroBigliettoMax(String id_evento) {
        int offset = 0;
        List<BigliettoEntity> biglietti = biglietto_repository.findBigliettoEntitiesByIdEvent(id_evento);

        for (BigliettoEntity biglietto : biglietti)
            if (offset < biglietto.getNBiglietto())
                offset = biglietto.getNBiglietto();

        return offset;
    }

    public boolean checkIfSerialNumberIsAlreadyTaken(String id_evento, int n) {
        List<BigliettoEntity> biglietti = biglietto_repository.findBigliettoEntitiesByIdEvent(id_evento);

        for (BigliettoEntity biglietto : biglietti)
            if (biglietto.getNBiglietto() == n)
                return true;

        return false;
    }
}
