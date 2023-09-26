package it.Entities.Biglietto.DTOs;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Prenotazione_DTO {
    @NotBlank
    private String idUtente;

    @NotBlank
    private String idBiglietto;
}
