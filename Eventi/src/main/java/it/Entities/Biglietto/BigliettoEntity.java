package it.Entities.Biglietto;

import it.Entities.Biglietto.DTOs.CreazioneBiglietto_DTO;
import it.Entities.Biglietto.DTOs.ModificaBiglietto_DTO;
import it.Entities.Position.Position;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.math.BigDecimal;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Document("Biglietti")
public class BigliettoEntity {
    @MongoId
    @Field(targetType = FieldType.OBJECT_ID)
    private String id;

    @Field(name = "ID_EVENTO")
    private String idEvent;

    @Field(name = "DATA_EVENTO")
    private Date data;

    @Field(name = "LOCATION")
    private Position location;

    @Field(name = "ID_UTENTE")
    private String idUtente;

    @Field(name = "NUMERO_BIGLIETTO")
    @Indexed(unique = true)
    private int nBiglietto;

    @Field(name = "PREZZO")
    private BigDecimal prezzo;

    public void setAll(CreazioneBiglietto_DTO dto) {
        idEvent = dto.getIdEvento();
        data = dto.getData();
        nBiglietto = dto.getNumeroBiglietti();
        prezzo = dto.getPrezzo();
    }

    public void setMod(ModificaBiglietto_DTO dto) {
        data = dto.getData();
        prezzo = dto.getPrezzo();
    }
}
