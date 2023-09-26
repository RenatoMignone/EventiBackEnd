package it.Services.CarPark;

import it.Entities.CarPark.CarPark;
import it.Entities.CarPark.DTOs.Create_CarPark_DTO;
import it.Entities.CarPark.DTOs.Modify_CarPark_DTO;
import it.Entities.Position.Position;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface CarPark_Service {
    //Utilities
    Create_CarPark_DTO mapToDTO(CarPark carPark);
    CarPark mapToEntity(Create_CarPark_DTO carParkDto);
    Boolean locationTaken(Position location);
    Boolean addressTaken(String address);


    //POST
    ResponseEntity<?> createCarPark(Create_CarPark_DTO carParkDto);


    //GET
    ResponseEntity<?> searchCarParksByNomeOrIndirizzo(String query);
    ResponseEntity<?> searchAvailableCarParksByNomeOrIndirizzoForEvento(String id_evento, String query);
    ResponseEntity<?> getAvailableCarParksByEvent(String id_evento);

    ResponseEntity<?> getCarParkById(String id);
    ResponseEntity<?> getCarParkByLocation(Position location);
    ResponseEntity<?>  getCarParkByAddress(String address);

    ResponseEntity<?> getCarParksByUserId(String userId);
    ResponseEntity<?> getAll();

    List<CarPark> getCarParksByName(String name);
    List<CarPark> getCarParksPublic();
    List<CarPark> getCarParksPrivate();


    //PUT
    ResponseEntity<?> modifyCarPark(String id, Modify_CarPark_DTO carParkDto);
    ResponseEntity<?> addEvento(String id_parcheggio, String id_evento);


    //DELETE
    ResponseEntity<?> deleteCarPark(String id);
    ResponseEntity<?> removeEvento(String id_parcheggio, String id_evento);
}
