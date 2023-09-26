package it.Entities.Biglietto.DTOs;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreazioneBiglietto_DTO {
    @NotBlank
    private String idEvento;

    @NotNull
    private Date data;

    @Min(1)
    private int numeroBiglietti;

    //@NotNull
    private BigDecimal prezzo;
}
