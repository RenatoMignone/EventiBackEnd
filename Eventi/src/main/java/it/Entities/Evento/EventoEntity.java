package it.Entities.Evento;

import it.Entities.Evento.DTOs.CreaEvento_DTO;
import it.Entities.Evento.DTOs.ModificaEvento_DTO;
import it.Entities.Position.Position;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.util.Date;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document("Eventi")
public class EventoEntity {
    @MongoId
    @Field(targetType = FieldType.OBJECT_ID)
    private String id;

    @Field(name = "ID_CREATORE")
    private String idCreatore;

    @Field(name = "NOME_EVENTO")
    private String nome;

    @Field(name = "DESCRIZIONE")
    private String descrizione;

    @Field(name = "DATA_E_ORA_INIZIO")
    private Date dataInizio;

    @Field(name = "DATA_E_ORA_FINE")
    private Date dataFine;

    @Field(name = "LOCATION")
    private Position location;

    @Field(name = "NOME_CATEGORIA")
    private String nomeCategoria;

    @Field(name = "ID_BIGLIETTI")
    private Set<String> idBiglietti;

    @Field(name = "ID_PARCHEGGI")
    private Set<String> idParcheggi;

    @Field(name = "ID_RECENSIONI")
    private Set<String> idRecensioni;

    @Field(name = "ID_PROGRAMMI")
    private Set<String> idProgrammi;


    public void setCreazione(CreaEvento_DTO dto){
        this.idCreatore = dto.getIdCreatore();
        this.nome = dto.getNome();
        this.descrizione = dto.getDescrizione();
        this.dataInizio = dto.getDataInizio();
        this.dataFine = dto.getDataFine();
        this.location = dto.getLocation();
        this.nomeCategoria = dto.getNomeCategoria();

        this.idBiglietti = new HashSet<>();
        this.idParcheggi = new HashSet<>();
        this.idRecensioni = new HashSet<>();
        this.idProgrammi = new HashSet<>();
    }

    public void setModifica(ModificaEvento_DTO dto){
        this.nome = dto.getNome();
        this.descrizione = dto.getDescrizione();
        this.dataInizio = dto.getDataInizio();
        this.dataFine = dto.getDataFine();
        this.location = dto.getLocation();

        this.nomeCategoria = dto.getNomeCategoria();
    }

    public boolean equals2(Object o) {
        if (this == o) return false;
        if (!(o instanceof EventoEntity that)) return true;

        return !Objects.equals(getNome(), that.getNome()) || !Objects.equals(getDataInizio(), that.getDataInizio())
                || !Objects.equals(getDataFine(), that.getDataFine()) || !Objects.equals(getLocation(), that.getLocation());
    }
}
