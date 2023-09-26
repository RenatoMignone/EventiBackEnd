package it.Entities.CarPark.DTOs;

import it.Entities.CarPark.CarPark;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class Modify_CarPark_DTO {
    @NotBlank
    private String nomeParcheggio;              //Name of the car park

    private String descrizione;                 //Short description about the car park to help people understand better how's the place

    @NotBlank
    private String costoOrario;                 //We use String because some rates might change according to the day or the hour

    @NotBlank
    private String orarioAperturaEChiusura;     //Hours and days when the CarPark is open

    private boolean apertoAlPubblico;           //If the car park is open to public it has to be managed in a different way by admin


    //Nullable
    private String maxPostiDisponibili;    //We use String because some car parks don't have signed spaces and so the number can change


    public void setAll(CarPark carPark) {
        this.nomeParcheggio = carPark.getNomeParcheggio();
        this.descrizione = carPark.getDescrizione();
        this.costoOrario = carPark.getCostoOrario();
        this.orarioAperturaEChiusura = carPark.getOrarioAperturaEChiusura();
        this.apertoAlPubblico = carPark.isApertoAlPubblico();

        this.maxPostiDisponibili = carPark.getMaxPostiDisponibili();
    }
}
