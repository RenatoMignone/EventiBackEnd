package it.Services.Immagine;


import it.Entities.Immagine.ImmagineEntity;
import it.Repositories.db.Eventi.Immagini_Repository;
import it.Services.Immagine.Utils.ImageUtils;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Log
public class Immagine_Service_impl implements Immagine_Service {
    Immagini_Repository immaginiRepo;
    @Autowired
    public Immagine_Service_impl(Immagini_Repository immaginiRepo) {
        this.immaginiRepo = immaginiRepo;
    }
//-------------------------------------------------------------------------------------------------------------------------------------------

    @Override
    public String uploadImage(MultipartFile file,String id_evento) throws IOException {
        ImmagineEntity immagine =(ImmagineEntity.builder()
                .nome(file.getOriginalFilename())
                .type(file.getContentType())
                .imageData(ImageUtils.compressImage(file.getBytes())).build());

        immagine.setIdevento(id_evento);
        immaginiRepo.save(immagine);
        if (immagine != null) {
            return "file uploaded successfully : " + file.getOriginalFilename();
        }
        return null;
    }
//-------------------------------------------------------------------------------------------------------------------------------------------

    @Override
    public byte[] downloadImage(String id) {
        Optional<ImmagineEntity> immagine = Optional.ofNullable(immaginiRepo.findById(id));
        return ImageUtils.decompressImage(immagine.get().getImageData());
    }
//-------------------------------------------------------------------------------------------------------------------------------------------

    @Override
    public List<String> getImmaginiEvento(String id_evento) {
        List<ImmagineEntity> immagini = immaginiRepo.findImmagineEntitiesByIdevento(id_evento);
        List<String> ids = new ArrayList<>();
        for(ImmagineEntity immagine : immagini){
            ids.add(immagine.getId());
        }
        return ids;
    }
//-------------------------------------------------------------------------------------------------------------------------------------------

    @Override
    public ResponseEntity<?> getSingleImage(String id) {
        return ResponseEntity.status(HttpStatus.OK).body(immaginiRepo.findById(id));
    }
//-------------------------------------------------------------------------------------------------------------------------------------------

    @Override
    public ResponseEntity<HttpStatus> modifyImage(String id, MultipartFile immagine) throws IOException {
        ImmagineEntity immagineEntity = immaginiRepo.findById(id);
        immagineEntity.setImageData(ImageUtils.compressImage(immagine.getBytes()));

        immaginiRepo.save(ImmagineEntity.builder()
                .nome(immagine.getOriginalFilename())
                .type(immagine.getContentType())
                .imageData(ImageUtils.compressImage(immagine.getBytes())).build());

        immaginiRepo.save(immagineEntity);
        return new ResponseEntity<>(HttpStatus.OK);
    }
//-------------------------------------------------------------------------------------------------------------------------------------------

    @Override
    public ResponseEntity<?> deleteImage(String id) {
        if(immaginiRepo.existsImmagineEntityById(id)){
            immaginiRepo.deleteImmagineEntityById(id);
            return new ResponseEntity<>(HttpStatus.OK);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("L'immagine che corrisponde all'id inserito non esiste");
    }

//-------------------------------------------------------------------------------------------------------------------------------------------

}
