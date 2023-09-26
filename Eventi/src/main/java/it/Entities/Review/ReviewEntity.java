package it.Entities.Review;

import it.Entities.Review.DTOs.Create_Review_DTO;
import it.Entities.Review.DTOs.Modify_Review_DTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;
import org.springframework.data.mongodb.core.mapping.MongoId;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document("Reviews")
// angular

public class ReviewEntity {
    @MongoId
    @Field(targetType = FieldType.OBJECT_ID)
    private String id;

    @Field(name = "ASSOCIATED_EVENT", targetType = FieldType.OBJECT_ID)
    private String idEvento;

    @Field(name = "USER_ID")
    private String idUtente;

    @Field(name = "NAME_AND_SURNAME")
    private String nomeCognome;

    @Field(name = "NUMBER_OF_STARS")
    private int numeroStelle;

    @Field(name = "TEXT")
    private String testo;

    public void setAll(Create_Review_DTO reviewDto) {
        this.idEvento = reviewDto.getIdEvento();
        this.idUtente = reviewDto.getIdUtente();
        this.numeroStelle = reviewDto.getNumeroStelle();
        this.testo = reviewDto.getTesto();
    }

    public void setAllMod(Modify_Review_DTO reviewDto) {
        this.numeroStelle = reviewDto.getNumeroStelle();
        this.testo = reviewDto.getTesto();
    }
}
