package it.Entities.Biglietto.DTOs;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ModificaBiglietto_DTO {
    @NotNull
    private Date data;

//    @NotNull
    private BigDecimal prezzo;
}
