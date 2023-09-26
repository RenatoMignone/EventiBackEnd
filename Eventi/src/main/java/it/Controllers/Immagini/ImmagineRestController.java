package it.Controllers.Immagini;

import com.mongodb.lang.Nullable;
import it.Entities.Immagine.ImmagineEntity;
import it.Services.Immagine.Immagine_Service_impl;
import lombok.extern.java.Log;
import org.bson.types.ObjectId;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@RestController
@CrossOrigin(origins = {"http://localhost:4200","http://localhost:9878","http://172.31.6.2:4200"})
@RequestMapping("api/v1/immagine")
public class ImmagineRestController {

    private final Immagine_Service_impl service;
    public ImmagineRestController(Immagine_Service_impl service) {
        this.service = service;
    }

//-------------------------------------------------------------------------------------------------------------------------------------------

//    @GetMapping("/bin/{id}")
//    public ResponseEntity<?> getSingleImage(@PathVariable String id){
//        return ResponseEntity.status(HttpStatus.OK).body(service.getSingleImage(id)) ;
//    }

//-------------------------------------------------------------------------------------------------------------------------------------------

    @PutMapping("/{id}")
    public ResponseEntity<?> modifyImage(@PathVariable String id, @RequestParam("image") MultipartFile image) throws IOException {
        return service.modifyImage(id,image);
    }

//-------------------------------------------------------------------------------------------------------------------------------------------

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteImage(@PathVariable String id){
        return service.deleteImage(id);
    }

//-------------------------------------------------------------------------------------------------------------------------------------------

    @PostMapping("evento/{id_evento}")
    public ResponseEntity<?> uploadImage(@RequestParam("image") MultipartFile file,@PathVariable String id_evento) throws IOException{
        String response = service.uploadImage(file,id_evento);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
//-------------------------------------------------------------------------------------------------------------------------------------------

    @GetMapping(value = "/evento/{id_evento}")
    public ResponseEntity<List<String>> getImmaginiEvento(@PathVariable String id_evento){
        return ResponseEntity.status(HttpStatus.OK).body(service.getImmaginiEvento(id_evento));
    }


//-------------------------------------------------------------------------------------------------------------------------------------------

    @GetMapping(value = "/bin/{id}", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<?> downloadImage(@PathVariable String id){
        byte[] imageData = service.downloadImage(id);
        return ResponseEntity.status(HttpStatus.OK).contentType(MediaType.valueOf("image/png")).body(imageData);
    }

//-------------------------------------------------------------------------------------------------------------------------------------------

}
