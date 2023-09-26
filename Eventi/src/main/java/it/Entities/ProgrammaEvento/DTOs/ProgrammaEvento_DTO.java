package it.Entities.ProgrammaEvento.DTOs;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Date;

@Data
public class ProgrammaEvento_DTO {
    @NotBlank
    private String idEvento;

    @NotNull
    private Date dataInizioProgramma;

    @NotBlank
    private String descrizione;
}
