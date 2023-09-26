package it.Repositories.db.Eventi;

import it.Entities.Categoria.CategoriaEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface Categoria_Mongo_Repository extends MongoRepository<CategoriaEntity, String> {
    CategoriaEntity findCategoriaEntityById(String id);
    Boolean existsCategoriaEntityByNomeCategoria(String nome);
}
