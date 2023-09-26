package it.Services.Immagine;


import it.Entities.Immagine.ImmagineEntity;
import org.bson.types.ObjectId;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

public interface Immagine_Service {
    String uploadImage(MultipartFile file,String id_evento) throws IOException;
    byte[] downloadImage(String id);
    List<String> getImmaginiEvento(String id_evento);
    ResponseEntity<?> getSingleImage(String id);
    ResponseEntity<HttpStatus> modifyImage(String id,MultipartFile immagine) throws IOException;
    ResponseEntity<?> deleteImage(String id);
}
