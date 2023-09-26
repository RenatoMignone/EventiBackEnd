package it.Controllers.Categoria;

import it.Entities.Categoria.CategoriaEntity;
import it.Entities.Categoria.DTOs.Categoria_DTO;
import it.Services.Categoria.Categoria_Service_Impl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = {"http://localhost:4200","http://localhost:9878","http://172.31.6.2:4200"})
@RequestMapping(value = "/api/v1/")
public class CategoriaRestController {
    final private Categoria_Service_Impl service;

    public CategoriaRestController(Categoria_Service_Impl service) {
        this.service = service;
    }
//-------------------------------------------------------------------------------------------------------------------------------------------

    @GetMapping("categoria")
    public  ResponseEntity<List<CategoriaEntity>> getAll(){
        return ResponseEntity.status(HttpStatus.OK).body(service.getCategorie());
    }
//-------------------------------------------------------------------------------------------------------------------------------------------

    @PutMapping("categoria/{id}")
    public ResponseEntity<?> modificaCategoria(@PathVariable String id, @RequestBody Categoria_DTO categoriaDto){
        if(service.verificaId(id)){
            if(!service.verifica(categoriaDto.getNomeCategoria()))
            {
                return service.modificaCategoria(id, categoriaDto.getNomeCategoria());
            }
            else{
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("la categoria che stai cercando di inserire già esiste.");
            }
        }
        else{
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("L'id inserito nell'url non corrisponde ad alcuna categoria");
        }
    }
//-------------------------------------------------------------------------------------------------------------------------------------------
    @DeleteMapping("/categoria/{id}")
    public ResponseEntity<?> eliminaCategoria(@PathVariable String id){
        if(service.verificaId(id)){
            return service.eliminaCategoria(id);
        }
        else{
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("La categoria inserita non esiste");
        }
    }
//-------------------------------------------------------------------------------------------------------------------------------------------

    @PostMapping("/categoria")
    public ResponseEntity<?> creaCategoriaAdmin(@RequestBody Categoria_DTO categoria){
        if(!service.verifica(categoria.getNomeCategoria())){
            return service.createCategoria(categoria);
        }
        else{
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("La categoria inserita è già esistente.");
        }
    }
//-------------------------------------------------------------------------------------------------------------------------------------------

}
