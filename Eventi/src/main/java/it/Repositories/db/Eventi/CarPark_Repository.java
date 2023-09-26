package it.Repositories.db.Eventi;

import it.Entities.CarPark.CarPark;
import it.Entities.Position.Position;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CarPark_Repository extends MongoRepository<CarPark, String> {
    Boolean existsCarParkByLocation(Position location);
    Boolean existsCarParkByIndirizzo(String indirizzo);

    CarPark findCarParkById(String id);
    CarPark findCarParkByLocation(Position location);
    CarPark findCarParkByIndirizzo(String indirizzo);

    List<CarPark> findCarParksByNomeParcheggio(String nomeParcheggio);
    List<CarPark> findCarParksByApertoAlPubblico(boolean apertoAlPubblico);
    List<CarPark> findCarParksByIdUtente(String idUtente);

    List<CarPark> findCarParksByNomeParcheggioContainingIgnoreCase(String query);
    List<CarPark> findCarParksByIndirizzoContainingIgnoreCase(String query);

    List<CarPark> findCarParksByIdUtenteAndApertoAlPubblicoIsFalse(String idUtente);

    void deleteCarParkById(String id);
}
