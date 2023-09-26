package it.Entities.Utente.DTOs;

import it.Entities.Utente.UserEntity;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Duration;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class User_RegisterDTO {
    @NotBlank
    private String username;

    @NotBlank
    private String password;

    @NotBlank
    private String nome;

    @NotBlank
    private String cognome;

    @NotBlank
    private String email;

    @NotNull
    private Date dataNascita;

    private String città;

    public User_RegisterDTO(String username, String password, String nome, String cognome, String email, List<String> userRoles) {
        this.username = username;
        this.password = password;
        this.nome = nome;
        this.cognome = cognome;
        this.email = email;
    }

    public void setAll(UserEntity userEntity){
        this.username= userEntity.getUsername();
        this.password= userEntity.getPassword();
        this.nome= userEntity.getNome();
        this.cognome= userEntity.getCognome();
        this.email= userEntity.getEmail();
        this.dataNascita=userEntity.getDataNascita();
        this.città=userEntity.getCittà();
    }

    public boolean checkUnderage() {
        Duration diff = Duration.between(dataNascita.toInstant().atZone(ZoneId.systemDefault()).toLocalDate().atStartOfDay(),
                LocalDate.now().atStartOfDay());

        return diff.toDays() < 365 * 18;
    }
}
