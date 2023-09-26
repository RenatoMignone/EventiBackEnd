package it.Entities.Categoria;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.MongoId;

@Data
@Document("Categorie")

// angular
public class CategoriaEntity {

    @MongoId
    private String id;

    @Field(name = "NOME_CATEGORIA")
    private String nomeCategoria;

}
