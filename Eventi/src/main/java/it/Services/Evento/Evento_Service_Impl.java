package it.Services.Evento;

import it.BasicServices.Pasquale.Mail.GmailEmailSender;
import it.Entities.Biglietto.BigliettoEntity;
import it.Entities.CarPark.CarPark;
import it.Entities.Evento.DTOs.CreaEvento_DTO;
import it.Entities.Evento.DTOs.ModificaEvento_DTO;
import it.Entities.Evento.EventoEntity;
import it.Entities.Utente.UserEntity;
import it.Repositories.db.Eventi.*;
import it.Repositories.db.Utente.User_Repository;
import it.Services.Utente.User_Service_Impl;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.bson.types.ObjectId;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.beanvalidation.SpringValidatorAdapter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class Evento_Service_Impl implements Evento_Service {
    private final Evento_Repository evento_repository;
    private final Biglietto_Repository biglietto_repository;
    private final User_Repository user_repository;
    private final Review_Repository review_repository;
    private final CarPark_Repository carPark_repository;
    private final Categoria_Mongo_Repository categoria_repository;
    private final Programma_Evento_Repository programma_evento_repository;

    private final GmailEmailSender emailSender;

    private final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    private final Validator validator = factory.getValidator();

    public Evento_Service_Impl(Evento_Repository eventoRepository, Biglietto_Repository bigliettoRepository, User_Repository userRepository, Review_Repository reviewRepository, CarPark_Repository carParkRepository, Categoria_Mongo_Repository categoriaRepository, Programma_Evento_Repository programmaEventoRepository, GmailEmailSender emailSender) {
        this.evento_repository = eventoRepository;
        this.biglietto_repository = bigliettoRepository;
        this.user_repository = userRepository;
        this.review_repository = reviewRepository;
        this.carPark_repository = carParkRepository;
        this.categoria_repository = categoriaRepository;
        this.programma_evento_repository = programmaEventoRepository;

        this.emailSender = emailSender;
    }

//-------------------------------------------------------------------------------------------------------------------------------------------
    //POST

    @Override
    public ResponseEntity<?> creaEvento(CreaEvento_DTO dto) {
        //Controlli
        //Validazione DTO
        SpringValidatorAdapter v = new SpringValidatorAdapter(validator);
        BeanPropertyBindingResult errors = new BeanPropertyBindingResult(dto, "dto");

        v.validate(dto, errors);
        if (errors.hasErrors())
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Missing mandatory fields");

        //Controlli su date
        if (dto.getDataInizio().after(dto.getDataFine()))
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Staring date can't be after ending date");

        if (dto.getDataFine().before(new Date()))
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("The event's ending is before current day");

        //Controllo esistenza creatore
        if (!user_repository.existsById(dto.getIdCreatore()))
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");

        //Controllo unicità evento
        if (evento_repository.existsEventoEntityByNomeAndDataInizioAndDataFineAndLocation(dto.getNome(),
                dto.getDataInizio(), dto.getDataFine(), dto.getLocation()))
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("There's already an event with the same " +
                    "name, dates and location. Please, change at least one of the attributes.");

        //Controllo esistenza categoria
        if (!categoria_repository.existsCategoriaEntityByNomeCategoria(dto.getNomeCategoria()))
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Category doesn't exist");


        //Creation
        EventoEntity evento = new EventoEntity();
        evento.setCreazione(dto);

        evento = evento_repository.save(evento);
        return ResponseEntity.status(HttpStatus.CREATED).body(evento);
    }

//-------------------------------------------------------------------------------------------------------------------------------------------
    //GET

    @Override
    public ResponseEntity<?> searchEventiByNome(String query) {
        List<EventoEntity> searchResults = evento_repository.findEventoEntitiesByNomeContainingIgnoreCase(query);
        if (searchResults.isEmpty())
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No events found with such a name");

        return ResponseEntity.status(HttpStatus.OK).body(searchResults);
    }

    @Override
    public ResponseEntity<?> getEventoById(String id) {
        if (!ObjectId.isValid(id))
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Not an ObjectId");

        EventoEntity evento = evento_repository.findEventoEntityById(id);
        if (evento == null)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Event not found");

        return ResponseEntity.status(HttpStatus.OK).body(evento);
    }

    @Override
    public ResponseEntity<?> getEventiByIdCreatore(String id_creatore) {
        if (!ObjectId.isValid(id_creatore))
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Not an ObjectId");

        if (!user_repository.existsById(id_creatore))
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");

        return ResponseEntity.status(HttpStatus.OK).body(evento_repository.findEventoEntitiesByIdCreatore(id_creatore));
    }

    @Override
    public ResponseEntity<?> getParcheggiAssociatiAEvento(String id_evento) {
        //Controllo validità ID
        if (!ObjectId.isValid(id_evento))
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Not an ObjectId");

        //Controllo esistenza evento
        EventoEntity evento = evento_repository.findEventoEntityById(id_evento);
        if (evento == null)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Event not found");

        //Prendo i parcheggi dalla repository e li inserisco nella lista da restituire
        List<CarPark> carParks = new ArrayList<>();
        for (String id : evento.getIdParcheggi())
            carParks.add(carPark_repository.findCarParkById(id));

        return ResponseEntity.status(HttpStatus.OK).body(carParks);
    }

    @Override
    public ResponseEntity<?> getAll() {
        return ResponseEntity.status(HttpStatus.OK).body(evento_repository.findAll());
    }

//-------------------------------------------------------------------------------------------------------------------------------------------
    //PUT

    @Override
    public ResponseEntity<?> modificaEvento(String id, ModificaEvento_DTO dto) {
        //Controlli
        //Validazione DTO
        SpringValidatorAdapter v = new SpringValidatorAdapter(validator);
        BeanPropertyBindingResult errors = new BeanPropertyBindingResult(dto, "dto");

        v.validate(dto, errors);
        if (errors.hasErrors())
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Missing mandatory fields");

        //Controllo su ObjectId
        if (!ObjectId.isValid(id))
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Not an ObjectId");

        //Controllo esistenza evento
        EventoEntity evento = evento_repository.findEventoEntityById(id);
        if (evento == null)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Event not found");

        //Controllo autenticazione
        UserEntity user = user_repository.findUserEntityById(evento.getIdCreatore());
        if (!User_Service_Impl.getAuthority2(user.getEmail()))
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not authorized");

        //Controlli su date
        if (dto.getDataInizio().after(dto.getDataFine()))
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Starting date can't be after ending date");

        if (dto.getDataFine().before(new Date()))
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("The event's ending is before current day");

        /*Controllo unicità evento. Se modifico uno degli attributi fondamentali, controllo se esiste già un altro
        * evento con gli stessi attributi. Se gli attributi fondamentali del dto sono uguali a quelli dell'evento
        * da modificare, allora vuol dire che sto modificando solo la descrizione e non sono necessari ulteriori
        * controlli sull'unicità*/
        EventoEntity newEvento = new EventoEntity();
        newEvento.setModifica(dto);
        if (evento.equals2(newEvento))
            if (evento_repository.existsEventoEntityByNomeAndDataInizioAndDataFineAndLocation(dto.getNome(),
                    dto.getDataInizio(), dto.getDataFine(), dto.getLocation()))
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("There's already another event with the same " +
                        "name, dates and location. Please, change at least one of the attributes.");


        //Comunicare ai proprietari dei biglietti che è stata effettuata una modifica degli attributi fondamentali dell'evento
        if (evento.equals2(newEvento)) {
            for (BigliettoEntity biglietto : biglietto_repository.findBigliettoEntitiesByIdEventAndIdUtenteIsNotNull(id)) {
                user = user_repository.findUserEntityById(biglietto.getIdUtente());
                emailSender.sendEmail(user.getEmail(), "Modifica evento " + evento.getNome(),
                        "Gentile utente,\n" +
                                "La informiamo che l'evento per cui Lei aveva prenotato un biglietto, " + evento.getNome()
                                + ", ha subito delle modifiche. A breve il Suo biglietto potrebbe essere modificato. " +
                                "Per maggiori informazioni consultare la pagina dell'evento o contattare l'organizzatore.\n" +
                                "Ci scusiamo per l'inconveniente.");
            }
        }

        evento.setModifica(dto);
        evento_repository.save(evento);
        return ResponseEntity.status(HttpStatus.OK).body(evento);
    }

//-------------------------------------------------------------------------------------------------------------------------------------------
    //DELETE

    @Override
    public ResponseEntity<?> cancellaEvento(String id) {
        //Controlli
        if (!ObjectId.isValid(id))
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Not an ObjectId");

        EventoEntity evento = evento_repository.findEventoEntityById(id);
        if (evento == null)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Event not found");

        //Controllo autenticazione
        UserEntity user = user_repository.findUserEntityById(evento.getIdCreatore());
        if (!User_Service_Impl.getAuthority2(user.getEmail()))
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not authorized");


        //Avviso ai proprietari dei biglietti
        for (BigliettoEntity biglietto : biglietto_repository.findBigliettoEntitiesByIdEventAndIdUtenteIsNotNull(id)) {
            user = user_repository.findUserEntityById(biglietto.getIdUtente());
            emailSender.sendEmail(user.getEmail(), "Cancellazione evento " + evento.getNome(),
                    "Gentile utente,\n" +
                            "La informiamo che l'evento per cui Lei aveva prenotato un biglietto, " + evento.getNome()
                            + ", è stato cancellato. Per maggiori informazioni contattare l'organizzatore.\n" +
                            "Ci scusiamo per l'inconveniente.");
        }
        biglietto_repository.deleteByIdEvent(id);

        //Rimozione recensioni
        review_repository.deleteByIdEvento(id);

        //Rimozione associazione parcheggi
        for (String id_parcheggio : evento.getIdParcheggi()) {
            CarPark carPark = carPark_repository.findCarParkById(id_parcheggio);
            carPark.getIdEventi().remove(id);
        }

        //Rimozioni programmi evento
        programma_evento_repository.deleteByIdEvento(id);

        evento_repository.deleteEventoEntityById(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
