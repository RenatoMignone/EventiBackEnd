package it.Entities.Utente;


import it.Entities.Utente.DTOs.User_RegisterDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.util.Date;
import java.util.List;

@Data //ci consente di non scrivere getters setters
@AllArgsConstructor //costruttore pieno
@NoArgsConstructor //costruttore vuoto
@Document("Utente") //Specifico in quale Documento del Database deve andare il mio utente
public class UserEntity {

    // in angular
    @MongoId
    @Field(targetType = FieldType.OBJECT_ID)
    private String id ;

    @Field(name = "USERID")
    @Indexed(unique = true)
    private String username;

    @Field(name="USER_PASSWORD") //ti permette di definire come il dato viene salvato nel database
    private String password;

    @Field(name="NAME")
    private String nome;

    @Field(name="SURNAME")
    private String cognome;

    @Field(name="EMAIL")
    @Indexed(unique = true)
    private String email;

    @Field(name="DATA_DI_NASCITA")
    private Date dataNascita;

    @Field(name="CITTA'_RESIDENZA")
    private String città;

    @Field(name = "RUOLO")
    private List<String> userRoles;

    @Field(name = "STATO_VERIFICA")
    private Boolean status;

    public void setAll(User_RegisterDTO userDTO) {
        this.username=userDTO.getUsername();
        this.password=userDTO.getPassword();
        this.nome=userDTO.getNome();
        this.cognome=userDTO.getCognome();
        this.email=userDTO.getEmail();
        this.dataNascita=userDTO.getDataNascita();
        this.città=userDTO.getCittà();
    }
}
