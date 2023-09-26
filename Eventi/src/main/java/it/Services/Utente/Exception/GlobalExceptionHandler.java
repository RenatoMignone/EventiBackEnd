package it.Services.Utente.Exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.util.Date;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<Error_Object> handleUserNotFoundException(UserNotFoundException ex, WebRequest request){
        Error_Object errore = new Error_Object();

        errore.setStatusCode(HttpStatus.NOT_FOUND.value());
        errore.setMessage(ex.getMessage());
        errore.setTimestamp(new Date());

        return new ResponseEntity<Error_Object>(errore,HttpStatus.NOT_FOUND);
    }
}
