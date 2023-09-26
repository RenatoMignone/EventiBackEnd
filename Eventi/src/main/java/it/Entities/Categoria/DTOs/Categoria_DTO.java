package it.Entities.Categoria.DTOs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;

@Data
@NoArgsConstructor
@AllArgsConstructor
// angular
public class Categoria_DTO {
    private String nomeCategoria;
}