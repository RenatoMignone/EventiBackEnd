package it.Entities.Review.DTOs;

import it.Entities.Review.ReviewEntity;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
//angular
public class Create_Review_DTO {
    @NotBlank
    private String idEvento;

    @NotBlank
    private String idUtente;

    @Min(0)
    @Max(10)
    private int numeroStelle;

    private String testo;

    public void setAll(ReviewEntity recensione){
        this.idEvento = recensione.getIdEvento();
        this.idUtente = recensione.getIdUtente();
        this.numeroStelle = recensione.getNumeroStelle();
        this.testo = recensione.getTesto();
    }
}
