package it.Services.ProgrammaEvento;

import it.Entities.Evento.EventoEntity;
import it.Entities.ProgrammaEvento.DTOs.ProgrammaEvento_DTO;
import it.Entities.ProgrammaEvento.ProgrammaEvento;
import it.Entities.Utente.UserEntity;
import it.Repositories.db.Eventi.Evento_Repository;
import it.Repositories.db.Eventi.Programma_Evento_Repository;
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

import java.util.List;

@Service
public class Programma_Evento_Service_impl implements Programma_Evento_Service{
    private final Programma_Evento_Repository programma_evento_repository;
    private final Evento_Repository evento_repository;
    private final User_Repository user_repository;

    private final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    private final Validator validator = factory.getValidator();

    public Programma_Evento_Service_impl(Programma_Evento_Repository programmaEventoRepository, Evento_Repository eventoRepository, User_Repository userRepository) {
        this.programma_evento_repository = programmaEventoRepository;
        evento_repository = eventoRepository;
        user_repository = userRepository;
    }

//-------------------------------------------------------------------------------------------------------------------------------------------
    //POST

    @Override
    public ResponseEntity<?> creaEvento(ProgrammaEvento_DTO dto) {
    //Controlli
        //Validazione DTO
        SpringValidatorAdapter v = new SpringValidatorAdapter(validator);
        BeanPropertyBindingResult errors = new BeanPropertyBindingResult(dto, "dto");

        v.validate(dto, errors);
        if (errors.hasErrors())
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Missing mandatory fields");

        //Controllo esistenza evento
        if (!ObjectId.isValid(dto.getIdEvento()))
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Not a valid ObjectId for event");

        EventoEntity evento = evento_repository.findEventoEntityById(dto.getIdEvento());
        if (evento == null)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Event not found");

        //Controllo su autenticazione
        UserEntity user = user_repository.findUserEntityById(evento.getIdCreatore());
        if (!User_Service_Impl.getAuthority2(user.getEmail()))
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not authorized");

        //Controllo su data
        if (!(dto.getDataInizioProgramma().compareTo(evento.getDataInizio()) >= 0
                && dto.getDataInizioProgramma().compareTo(evento.getDataFine()) <= 0) )
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Program's starting date must be between the event's dates");


    //Creazione
        ProgrammaEvento programma = new ProgrammaEvento();
        programma.setAll(dto);
        programma = programma_evento_repository.save(programma);

        evento.getIdProgrammi().add(programma.getId());
        evento_repository.save(evento);

        return ResponseEntity.status(HttpStatus.OK).body(programma);
    }
//-------------------------------------------------------------------------------------------------------------------------------------------
    //GET

    @Override
    public ResponseEntity<?> getProgramma(String id) {
        //Controllo path
        if (!ObjectId.isValid(id))
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Not an ObjectId");

        //Controllo esistenza programma
        ProgrammaEvento programma = programma_evento_repository.findProgrammaEventoById(id);
        if (programma == null)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Program not found");

        return ResponseEntity.status(HttpStatus.OK).body(programma);
    }

    @Override
    public ResponseEntity<?> getProgrammiEventoDaIdEvento(String id_evento) {
        //Controllo path
        if (!ObjectId.isValid(id_evento))
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Not an ObjectId");

        //Controllo esistenza evento
        if (!evento_repository.existsById(id_evento))
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Event not found");

        List<ProgrammaEvento> programma = programma_evento_repository.findProgrammaEventosByIdEvento(id_evento);
        return ResponseEntity.status(HttpStatus.OK).body(programma);
    }
//-------------------------------------------------------------------------------------------------------------------------------------------
    //PUT

    @Override
    public ResponseEntity<?> modificaProgramma(String id, ProgrammaEvento_DTO dto) {
    //Controlli
        //Controllo path
        if (!ObjectId.isValid(id))
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Not an ObjectId");

        //Controllo esistenza programma
        ProgrammaEvento programma = programma_evento_repository.findProgrammaEventoById(id);
        if (programma == null)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Program not found");

        //Validazione DTO
        SpringValidatorAdapter v = new SpringValidatorAdapter(validator);
        BeanPropertyBindingResult errors = new BeanPropertyBindingResult(dto, "dto");

        v.validate(dto, errors);
        if (errors.hasErrors())
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Missing mandatory fields");

        //Controllo esistenza evento
        if (!ObjectId.isValid(dto.getIdEvento()))
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Not a valid ObjectId for event");

        EventoEntity evento = evento_repository.findEventoEntityById(dto.getIdEvento());
        if (evento == null)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Event not found");

        //Blocco cambio evento
        if (!evento.getId().equals(programma.getIdEvento()))
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("It's forbidden to change event");

        //Controllo su autenticazione
        UserEntity user = user_repository.findUserEntityById(evento.getIdCreatore());
        if (!User_Service_Impl.getAuthority2(user.getEmail()))
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not authorized");

        //Controllo su data
        if (!(dto.getDataInizioProgramma().compareTo(evento.getDataInizio()) >= 0
                && dto.getDataInizioProgramma().compareTo(evento.getDataFine()) <= 0) )
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Program's starting date must be between the event's dates");


    //Modifica
        programma.setAll(dto);
        programma_evento_repository.save(programma);
        return ResponseEntity.status(HttpStatus.OK).body(programma);
    }
//-------------------------------------------------------------------------------------------------------------------------------------------
    //DELETE
    @Override
    public ResponseEntity<?> deleteProgramma(String id) {
    //Controlli
        //Controllo path
        if (!ObjectId.isValid(id))
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Not an ObjectId");

        //Controllo esistenza programma
        ProgrammaEvento programma = programma_evento_repository.findProgrammaEventoById(id);
        if (programma == null)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Program not found");

        //Controllo esistenza evento
        EventoEntity evento = evento_repository.findEventoEntityById(programma.getIdEvento());
        if (evento == null)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Event not found");

        //Controllo su autenticazione
        UserEntity user = user_repository.findUserEntityById(evento.getIdCreatore());
        if (!User_Service_Impl.getAuthority2(user.getEmail()))
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not authorized");


    //Eliminazione
        evento.getIdProgrammi().remove(id);
        evento_repository.save(evento);

        programma_evento_repository.deleteById(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}