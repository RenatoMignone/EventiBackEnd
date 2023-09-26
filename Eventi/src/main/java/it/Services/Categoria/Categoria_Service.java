package it.Services.Categoria;

import it.Entities.Categoria.CategoriaEntity;
import it.Entities.Categoria.DTOs.Categoria_DTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface Categoria_Service {
    List<CategoriaEntity> getCategorie();
    ResponseEntity<?> createCategoria(Categoria_DTO categoriaDto);
    ResponseEntity<?> modificaCategoria(String id,String nuovoNome);
    ResponseEntity<HttpStatus> eliminaCategoria(String id);
    Boolean verifica(String nome);
    Boolean verificaId(String id);
}
