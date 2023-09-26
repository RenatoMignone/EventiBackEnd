package it.Entities.ProgrammaEvento;

import it.Entities.ProgrammaEvento.DTOs.ProgrammaEvento_DTO;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.util.Date;

@Data
@Document("programmaEvento")
public class ProgrammaEvento {
    @MongoId
    @Field(targetType = FieldType.OBJECT_ID)
    private String id;

    @Field(name = "ID_EVENTO")
    private String idEvento;

    @Field(name = "DATA_INIZIO_PROGRAMMA")
    private Date dataInizioProgramma;

    @Field(name = "DESCRIZIONE")
    private String descrizione;

    public void setAll(ProgrammaEvento_DTO dto) {
        this.idEvento = dto.getIdEvento();
        this.dataInizioProgramma = dto.getDataInizioProgramma();
        this.descrizione = dto.getDescrizione();
    }
}
