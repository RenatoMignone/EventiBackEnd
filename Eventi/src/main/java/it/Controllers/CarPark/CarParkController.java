package it.Controllers.CarPark;

import it.Entities.CarPark.DTOs.Create_CarPark_DTO;
import it.Entities.CarPark.DTOs.Modify_CarPark_DTO;
import it.Services.CarPark.CarPark_Service_Implementation;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Log
@RestController
@RequestMapping("api/v1/" + "parcheggio")
@CrossOrigin(origins = {"http://localhost:4200","http://localhost:9878","http://172.31.6.2:4200"})
public class CarParkController {
    private final CarPark_Service_Implementation service;

    @Autowired
    public CarParkController(CarPark_Service_Implementation service) {
        this.service = service;
    }

//----------------------------------------------------------------------------------------------------------------------
    //POST
    @PostMapping()
    public ResponseEntity<?> create (@RequestBody Create_CarPark_DTO carParkDto) {
        return service.createCarPark(carParkDto);
    }

//----------------------------------------------------------------------------------------------------------------------
    //GET
    @GetMapping("search")
    public ResponseEntity<?> searchByNomeOrIndirizzo(@RequestParam("query") String query) {
        return service.searchCarParksByNomeOrIndirizzo(query);
    }

    @GetMapping("evento/{id_evento}/search")
    public ResponseEntity<?> searchAvailableCarParksByNomeOrIndirizzoForEvento(@PathVariable String id_evento, @RequestParam("query") String query) {
        return service.searchAvailableCarParksByNomeOrIndirizzoForEvento(id_evento, query);
    }

    @GetMapping("evento/{id_evento}")
    public ResponseEntity<?> getAvailableCarParksByEvent(@PathVariable String id_evento) {
        return service.getAvailableCarParksByEvent(id_evento);
    }

    @GetMapping("{idParcheggio}")
    public ResponseEntity<?> getById(@PathVariable String idParcheggio) {
        return service.getCarParkById(idParcheggio);
    }

    @GetMapping("indirizzo/{indirizzo}")
    public ResponseEntity<?> getByAddress (@PathVariable String indirizzo) {
        return service.getCarParkByAddress(indirizzo);
    }

    @GetMapping("utente/{idUtente}")
    public ResponseEntity<?> getCarParksByUserId(@PathVariable String idUtente) {
        return service.getCarParksByUserId(idUtente);
    }

    @GetMapping("getAll")
    public ResponseEntity<?> getAll() {
        return service.getAll();
    }

//----------------------------------------------------------------------------------------------------------------------
    //PUT
    @PutMapping("{idParcheggio}")
    public ResponseEntity<?> modify (@RequestBody Modify_CarPark_DTO carParkDto, @PathVariable String idParcheggio) {
        return service.modifyCarPark(idParcheggio, carParkDto);
    }

    @PutMapping("{idParcheggio}/evento/{idEvento}")
    public ResponseEntity<?> addEvento (@PathVariable String idParcheggio, @PathVariable String idEvento) {
        return service.addEvento(idParcheggio, idEvento);
    }

//----------------------------------------------------------------------------------------------------------------------
    //DELETE
    @DeleteMapping("{idParcheggio}")
    public ResponseEntity<?> delete (@PathVariable String idParcheggio) {
        return service.deleteCarPark(idParcheggio);
    }

    @DeleteMapping("{idParcheggio}/evento/{idEvento}")
    public ResponseEntity<?> removeEvento (@PathVariable String idParcheggio, @PathVariable String idEvento) {
        return service.removeEvento(idParcheggio, idEvento);
    }
}
