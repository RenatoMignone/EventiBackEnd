package it.Entities.CarPark.DTOs;

import it.Entities.CarPark.CarPark;
import it.Entities.Position.Position;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Mattia Marino
 * Created on: May 8, 2023
 * Last modified on: May 28, 2023
 */


@Data
@AllArgsConstructor
@NoArgsConstructor
public class Create_CarPark_DTO {
    @NotBlank
    private String nomeParcheggio;              //Name of the CarPark

    @NotBlank
    private String indirizzo;                   //Address of the CarPark

    private String descrizione;                 //Short description about the car park to help people understand better how's the place

    @NotBlank
    private String costoOrario;                 //We use String because some rates might change according to the day or the hour

    @NotBlank
    private String orarioAperturaEChiusura;     //Hours and days when the CarPark is open

    private boolean apertoAlPubblico;           //If the car park is open to public it has to be managed in a different way by admin

    @NotBlank
    private String idUtente;                    //ID of the user who created the CarPark and can perform operations on it

    @NotNull
    private Position location;


    //Nullable
    private String maxPostiDisponibili;    //We use String because some car parks don't have signed spaces and so the number can change


    public void setAll(CarPark carPark) {
        this.nomeParcheggio = carPark.getNomeParcheggio();
        this.indirizzo = carPark.getIndirizzo();
        this.descrizione = carPark.getDescrizione();
        this.costoOrario = carPark.getCostoOrario();
        this.orarioAperturaEChiusura = carPark.getOrarioAperturaEChiusura();
        this.apertoAlPubblico = carPark.isApertoAlPubblico();
        this.idUtente = carPark.getIdUtente();
        this.location = carPark.getLocation();

        this.maxPostiDisponibili = carPark.getMaxPostiDisponibili();
    }
}
