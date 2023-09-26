package it.Entities.CarPark;


import it.Entities.CarPark.DTOs.Create_CarPark_DTO;
import it.Entities.CarPark.DTOs.Modify_CarPark_DTO;
import it.Entities.Position.Position;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.util.HashSet;
import java.util.Set;

//TODO modificare in angular

@Data       //No need to write all getters and setters
@AllArgsConstructor
@NoArgsConstructor
@Document(value = "CarPark")//Position in database
public class CarPark {
    @MongoId
    @Field(targetType = FieldType.OBJECT_ID)
    private String id;

    @Field(name = "NOME_PARCHEGGIO")
    private String nomeParcheggio;                //Name of the CarPark

    @Field(name = "INDIRIZZO")
    @Indexed(unique = true)
    private String indirizzo;             //Address of the CarPark

    @Field(name = "DESCRIZIONE")
    private String descrizione;         //Short description about the car park to help people understand better how's the place

    @Field(name = "COSTO_ORARIO")
    private String costoOrario;          //We use String because some rates might change according to the day or the hour

    @Field(name = "ORARIO_APERTURA_E_CHIUSURA")
    private String orarioAperturaEChiusura;        //Hours and days when the CarPark is open

    @Field(name = "APERTO_AL_PUBBLICO")
    private boolean apertoAlPubblico;       //If the car park is open to public it has to be managed in a different way by admin

    @Field(name = "ID_EVENTI_ASSOCIATI")
    private Set<String> idEventi;      //List of event IDs associated to a single CarPark

    @Field(name = "ID_CREATORE")
    private String idUtente;              //ID of the user who created the CarPark and can perform operations on it

    @Field(name = "LOCATION")
    private Position location;

    @Field(name = "NUMBER_OF_PARKING_SPACES")
    private String maxPostiDisponibili;     //We use String because some car parks don't have signed spaces and so the number can change


    public void setAll(Create_CarPark_DTO carParkDto) {
        this.nomeParcheggio = carParkDto.getNomeParcheggio();
        this.indirizzo = carParkDto.getIndirizzo();
        this.descrizione = carParkDto.getDescrizione();
        this.costoOrario = carParkDto.getCostoOrario();
        this.orarioAperturaEChiusura = carParkDto.getOrarioAperturaEChiusura();
        this.apertoAlPubblico = carParkDto.isApertoAlPubblico();
        this.idUtente = carParkDto.getIdUtente();
        this.location = carParkDto.getLocation();

        this.maxPostiDisponibili = carParkDto.getMaxPostiDisponibili();

        this.idEventi = new HashSet<>();
    }

    public void setAllMod(Modify_CarPark_DTO carParkDto) {
        this.nomeParcheggio = carParkDto.getNomeParcheggio();
        this.descrizione = carParkDto.getDescrizione();
        this.costoOrario = carParkDto.getCostoOrario();
        this.orarioAperturaEChiusura = carParkDto.getOrarioAperturaEChiusura();
        this.apertoAlPubblico = carParkDto.isApertoAlPubblico();

        this.maxPostiDisponibili = carParkDto.getMaxPostiDisponibili();
    }
}
