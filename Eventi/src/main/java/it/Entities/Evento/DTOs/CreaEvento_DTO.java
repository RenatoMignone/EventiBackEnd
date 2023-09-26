package it.Entities.Evento.DTOs;

import it.Entities.Position.Position;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreaEvento_DTO {
    @NotBlank
    private String idCreatore;

    @NotBlank
    private String nome;

    private String descrizione;

    @NotNull
    private Date dataInizio;

    @NotNull
    private Date dataFine;

    @NotNull
    private Position location;

    @NotNull
    private String nomeCategoria;
}
