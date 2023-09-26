package it.Services.Categoria;

import it.Entities.Categoria.CategoriaEntity;
import it.Entities.Categoria.DTOs.Categoria_DTO;
import it.Repositories.db.Eventi.Categoria_Mongo_Repository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class Categoria_Service_Impl implements Categoria_Service{

    private final Categoria_Mongo_Repository categoria_repository;

    public Categoria_Service_Impl(Categoria_Mongo_Repository categoria_repository) {
        this.categoria_repository = categoria_repository;
    }
//-------------------------------------------------------------------------------------------------------------------------------------------

    @Override
    public List<CategoriaEntity> getCategorie() {
        return categoria_repository.findAll();
    }
//-------------------------------------------------------------------------------------------------------------------------------------------

    @Override
    public ResponseEntity<?> createCategoria(Categoria_DTO categoriaDto) {
        CategoriaEntity categoria = new CategoriaEntity();
        categoria.setNomeCategoria(categoriaDto.getNomeCategoria());
        categoria = categoria_repository.save(categoria);
        return ResponseEntity.status(HttpStatus.OK).body(categoria);
    }
//-------------------------------------------------------------------------------------------------------------------------------------------

    @Override
    public ResponseEntity<?> modificaCategoria(String id, String nuovoNome) {
        CategoriaEntity categoria = categoria_repository.findCategoriaEntityById(id);
        categoria.setNomeCategoria(nuovoNome);
        categoria = categoria_repository.save(categoria);
        return ResponseEntity.status(HttpStatus.OK).body(categoria);
    }
//-------------------------------------------------------------------------------------------------------------------------------------------

    @Override
    public ResponseEntity<HttpStatus> eliminaCategoria(String id) {
        categoria_repository.deleteById(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }
//-------------------------------------------------------------------------------------------------------------------------------------------

    @Override
    public Boolean verifica(String nome) {
        return categoria_repository.existsCategoriaEntityByNomeCategoria(nome);
    }

    @Override
    public Boolean verificaId(String id) {
        return categoria_repository.existsById(id);
    }
//-------------------------------------------------------------------------------------------------------------------------------------------

}
