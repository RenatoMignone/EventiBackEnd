package it.Services.Utente.Exception;

import lombok.Data;

import java.util.Date;

@Data
public class Error_Object {
    private Integer statusCode;
    private String message;
    private Date timestamp;
}
