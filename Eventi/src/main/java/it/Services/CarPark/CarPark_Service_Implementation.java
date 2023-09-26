package it.Services.CarPark;

import it.Entities.CarPark.CarPark;
import it.Entities.CarPark.DTOs.Create_CarPark_DTO;
import it.Entities.CarPark.DTOs.Modify_CarPark_DTO;
import it.Entities.Evento.EventoEntity;
import it.Entities.Position.Position;
import it.Entities.Utente.UserEntity;
import it.Repositories.db.Eventi.CarPark_Repository;
import it.Repositories.db.Eventi.Evento_Repository;
import it.Repositories.db.Utente.User_Repository;
import it.Services.Utente.User_Service_Impl;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import lombok.extern.java.Log;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.beanvalidation.SpringValidatorAdapter;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Log
@Service
public class CarPark_Service_Implementation implements CarPark_Service {
    private final CarPark_Repository carPark_repository;
    private final User_Repository user_repository;
    private final Evento_Repository evento_repository;

    private final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    private final Validator validator = factory.getValidator();

    @Autowired
    public CarPark_Service_Implementation(CarPark_Repository carParkRepository, User_Repository userRepository, Evento_Repository eventoRepository) {
        this.carPark_repository = carParkRepository;
        user_repository = userRepository;
        evento_repository = eventoRepository;
    }

//----------------------------------------------------------------------------------------------------------------------
    //Utilities
    @Override
    public Create_CarPark_DTO mapToDTO(CarPark carPark) {
        Create_CarPark_DTO carParkDto = new Create_CarPark_DTO();
        carParkDto.setAll(carPark);
        return carParkDto;
    }

    @Override
    public CarPark mapToEntity(Create_CarPark_DTO carParkDto) {
        CarPark carPark = new CarPark();
        carPark.setAll(carParkDto);
        return carPark;
    }

    @Override
    public Boolean locationTaken(Position location) {
        return carPark_repository.existsCarParkByLocation(location);
    }

    @Override
    public Boolean addressTaken(String address) {
        return carPark_repository.existsCarParkByIndirizzo(address);
    }

//----------------------------------------------------------------------------------------------------------------------
    //POST
    @Override
    public ResponseEntity<?> createCarPark(Create_CarPark_DTO carParkDto) {
        //Validazione DTO
        SpringValidatorAdapter v = new SpringValidatorAdapter(validator);
        BeanPropertyBindingResult errors = new BeanPropertyBindingResult(carParkDto, "carParkDto");

        v.validate(carParkDto, errors);
        if (errors.hasErrors())
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Missing mandatory fields");

        //Controllo esistenza utente creatore
        if (!user_repository.existsById(carParkDto.getIdUtente()))
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Couldn't find the user who created the car park");

        //Controllo esistenza parcheggio
        if (carPark_repository.existsCarParkByIndirizzo(carParkDto.getIndirizzo()))
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("There is already a car park with the same address");
        if (carPark_repository.existsCarParkByLocation(carParkDto.getLocation()))
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("There is already a car park with the same location");


        //Creazione parcheggio
        CarPark carPark = new CarPark();
        carPark.setAll(carParkDto);

        carPark = carPark_repository.save(carPark);
        return ResponseEntity.status(HttpStatus.CREATED).body(carPark);
    }

//----------------------------------------------------------------------------------------------------------------------
    //GET

    @Override
    public ResponseEntity<?> searchCarParksByNomeOrIndirizzo(String query) {
        List<CarPark> searchResults = carPark_repository.findCarParksByNomeParcheggioContainingIgnoreCase(query);
        if (searchResults.isEmpty()) {
            searchResults = carPark_repository.findCarParksByIndirizzoContainingIgnoreCase(query);

            if (searchResults.isEmpty())
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No car parks found with such a name or address");
        }

        return ResponseEntity.status(HttpStatus.OK).body(searchResults);
    }

    @Override
    public ResponseEntity<?> searchAvailableCarParksByNomeOrIndirizzoForEvento(String id_evento, String query) {
    //Controlli
        //Controllo validità ID
        if (!ObjectId.isValid(id_evento))
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Not an ObjectId");

        //Controllo esistenza evento
        EventoEntity evento = evento_repository.findEventoEntityById(id_evento);
        if (evento == null)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Event not found");

        //Controllo autenticazione
        UserEntity user = user_repository.findUserEntityById(evento.getIdCreatore());
        if (!User_Service_Impl.getAuthority2(user.getEmail()))
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not authorized");

    //Get and order all available the car parks
        List<CarPark> privateCarParks = carPark_repository.findCarParksByIdUtenteAndApertoAlPubblicoIsFalse(user.getId());
        List<CarPark> publicCarParks = carPark_repository.findCarParksByApertoAlPubblico(true);

        // Sort the lists based on proximity
        privateCarParks.sort(new ProximityComparator(evento.getLocation()));
        publicCarParks.sort(new ProximityComparator(evento.getLocation()));

        //Merge the two lists together and send
        List<CarPark> carParks = new ArrayList<>();
        carParks.addAll(privateCarParks);
        carParks.addAll(publicCarParks);

        List<CarPark> searchResults = new ArrayList<>();
        for (CarPark carPark : carParks)
            if (carPark.getNomeParcheggio().toLowerCase().contains(query.toLowerCase()) ||
                    carPark.getIndirizzo().toLowerCase().contains(query.toLowerCase()))
                searchResults.add(carPark);

        return ResponseEntity.status(HttpStatus.OK).body(searchResults);
    }

    @Override
    public ResponseEntity<?> getAvailableCarParksByEvent(String id_evento) {
    //Controlli
        //Controllo validità ID
        if (!ObjectId.isValid(id_evento))
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Not an ObjectId");

        //Controllo esistenza evento
        EventoEntity evento = evento_repository.findEventoEntityById(id_evento);
        if (evento == null)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Event not found");

        //Controllo autenticazione
        UserEntity user = user_repository.findUserEntityById(evento.getIdCreatore());
        if (!User_Service_Impl.getAuthority2(user.getEmail()))
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not authorized");

    //Get and order the car parks
        List<CarPark> privateCarParks = carPark_repository.findCarParksByIdUtenteAndApertoAlPubblicoIsFalse(user.getId());
        List<CarPark> publicCarParks = carPark_repository.findCarParksByApertoAlPubblico(true);

        // Sort the lists based on proximity
        privateCarParks.sort(new ProximityComparator(evento.getLocation()));
        publicCarParks.sort(new ProximityComparator(evento.getLocation()));

        //Merge the two lists together and send
        List<CarPark> mergedCarParks = new ArrayList<>();
        mergedCarParks.addAll(privateCarParks);
        mergedCarParks.addAll(publicCarParks);

        return ResponseEntity.status(HttpStatus.OK).body(mergedCarParks);
    }

    @Override
    public ResponseEntity<?> getCarParkById(String id) {
        if (!ObjectId.isValid(id))
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Not an ObjectId");

        CarPark carPark = carPark_repository.findCarParkById(id);
        if (carPark == null)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Car park not found. Try with a different ID");

        return ResponseEntity.status(HttpStatus.OK).body(carPark);
    }

    @Override
    public ResponseEntity<?>  getCarParkByLocation(Position location) {
        CarPark carPark = carPark_repository.findCarParkByLocation(location);

        if (carPark == null)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Car park not found. Try with a different location");

        return ResponseEntity.status(HttpStatus.OK).body(carPark);
    }

    @Override
    public ResponseEntity<?> getCarParkByAddress(String address) {
        CarPark carPark = carPark_repository.findCarParkByIndirizzo(address);

        if (carPark == null)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Car park not found. Try with a different address");

        return ResponseEntity.status(HttpStatus.OK).body(carPark);
    }

    @Override
    public ResponseEntity<?> getCarParksByUserId(String userId) {
        if (!ObjectId.isValid(userId))
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        UserEntity user = user_repository.findUserEntityById(userId);
        if (user == null)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found, try with a different ID");

        //Control on authentication
        if (!User_Service_Impl.getAuthority2(user.getEmail()))
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not authorized");

        List<CarPark> carParks = carPark_repository.findCarParksByIdUtente(userId);

        return ResponseEntity.status(HttpStatus.OK).body(carParks);
    }

    @Override
    public ResponseEntity<?> getAll() {
        return ResponseEntity.status(HttpStatus.OK).body(carPark_repository.findAll());
    }

    //Not used yet
    @Override
    public List<CarPark> getCarParksByName(String name) {
        return carPark_repository.findCarParksByNomeParcheggio(name);
    }

    @Override
    public List<CarPark> getCarParksPublic() {
        return carPark_repository.findCarParksByApertoAlPubblico(true);
    }

    @Override
    public List<CarPark> getCarParksPrivate() {
        return carPark_repository.findCarParksByApertoAlPubblico(false);
    }

//----------------------------------------------------------------------------------------------------------------------
    //PUT
    @Override
    public ResponseEntity<?> modifyCarPark(String id, Modify_CarPark_DTO carParkDto) {
        //Validazione DTO
        SpringValidatorAdapter v = new SpringValidatorAdapter(validator);
        BeanPropertyBindingResult errors = new BeanPropertyBindingResult(carParkDto, "carParkDto");

        v.validate(carParkDto, errors);
        if (errors.hasErrors())
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Missing mandatory fields");

        //Controlli
        if (!ObjectId.isValid(id))
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        CarPark carPark = carPark_repository.findCarParkById(id);
        if (carPark == null)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Nonexistent car park id");

        //Control on authentication
        UserEntity user = user_repository.findUserEntityById(carPark.getIdUtente());
        if (!User_Service_Impl.getAuthority2(user.getEmail()))
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not authorized");

        carPark.setAllMod(carParkDto);
        carPark_repository.save(carPark);
        return ResponseEntity.status(HttpStatus.OK).body(carPark);
    }

    @Override
    public ResponseEntity<?> addEvento(String id_parcheggio, String id_evento) {
        //Controlli
        if (!ObjectId.isValid(id_parcheggio) || !ObjectId.isValid(id_evento))
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Not an ObjectId");

        CarPark carPark = carPark_repository.findCarParkById(id_parcheggio);
        if (carPark == null)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Car park not found");

        EventoEntity evento = evento_repository.findEventoEntityById(id_evento);
        if (evento == null)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Event not found");

        //Controlli autenticazione
        UserEntity user = user_repository.findUserEntityById(evento.getIdCreatore());
        if (!User_Service_Impl.getAuthority2(user.getEmail()))
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not authorized");

        //Controllo autorità su parcheggio (pubblico o privato)
        if (!carPark.isApertoAlPubblico()) {
            user = user_repository.findUserEntityById(carPark.getIdUtente());
            if (!User_Service_Impl.getAuthority2(user.getEmail()))
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not authorized");
        }

        //Aggiunta
        if (carPark.getIdEventi().contains(id_evento))
            return new ResponseEntity<>(HttpStatus.OK);

        carPark.getIdEventi().add(id_evento);
        evento.getIdParcheggi().add(id_parcheggio);
        carPark_repository.save(carPark);
        evento_repository.save(evento);
        return ResponseEntity.status(HttpStatus.OK).body(carPark);
    }

//----------------------------------------------------------------------------------------------------------------------
    //DELETE
    @Override
    public ResponseEntity<?> deleteCarPark(String id) {
        if (!ObjectId.isValid(id))
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        CarPark carPark = carPark_repository.findCarParkById(id);
        if (carPark == null)
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        //Control on authentication
        UserEntity user = user_repository.findUserEntityById(carPark.getIdUtente());
        if (!User_Service_Impl.getAuthority2(user.getEmail()))
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not authorized");


        //Try safe delete
        EventoEntity evento;
        for (String id_evento : carPark.getIdEventi()) {
            evento = evento_repository.findEventoEntityById(id_evento);

            if (evento != null) {
                evento.getIdParcheggi().remove(id);
                evento_repository.save(evento);
            }
        }

        carPark_repository.deleteCarParkById(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Override
    public ResponseEntity<?> removeEvento(String id_parcheggio, String id_evento) {
        //Controlli
        if (!ObjectId.isValid(id_parcheggio) || !ObjectId.isValid(id_evento))
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Not an ObjectId");

        CarPark carPark = carPark_repository.findCarParkById(id_parcheggio);
        if (carPark == null)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Car park not found");

        EventoEntity evento = evento_repository.findEventoEntityById(id_evento);
        if (evento == null)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Event not found");

        //Controlli autenticazione
        UserEntity user = user_repository.findUserEntityById(evento.getIdCreatore());
        if (!User_Service_Impl.getAuthority2(user.getEmail()))
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not authorized");

        //Rimozione
        if (!carPark.getIdEventi().contains(id_evento))
            return new ResponseEntity<>(HttpStatus.OK);

        carPark.getIdEventi().remove(id_evento);
        evento.getIdParcheggi().remove(id_parcheggio);
        carPark_repository.save(carPark);
        evento_repository.save(evento);
        return new ResponseEntity<>(HttpStatus.OK);
    }

//----------------------------------------------------------------------------------------------------------------------
    //Utilities
    static class ProximityComparator implements Comparator<CarPark> {
        private final Position target;

        public ProximityComparator(Position target) {
            this.target = target;
        }

        @Override
        public int compare(CarPark cp1, CarPark cp2) {
            double distance1 = calculateDistance(cp1.getLocation(), target);
            double distance2 = calculateDistance(cp2.getLocation(), target);

            // Compare distances
            return Double.compare(distance1, distance2);
        }

        private double calculateDistance(Position p1, Position p2) {
            // Implement your distance calculation logic here
            // You can use the Haversine formula or any other suitable method

            // For simplicity, let's assume a simple Euclidean distance calculation
            double latDiff = p1.getLat() - p2.getLat();
            double lngDiff = p1.getLng() - p2.getLng();
            return Math.sqrt(latDiff * latDiff + lngDiff * lngDiff);
        }
    }
}
