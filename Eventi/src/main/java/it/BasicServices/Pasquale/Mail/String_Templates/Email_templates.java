package it.BasicServices.Pasquale.Mail.String_Templates;

import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

@NoArgsConstructor
@Component
public class Email_templates
{
    private String object;
    private String body;

    public String Object_password_recovery(){
        return this.object = "Password Recovery";
    }

    public String Object_Registration_verification(){
        return this.object = "Email di Verifica";
    }

    public String Body_Registration_verification() {
        return this.body = "Di seguito il link per verificare il tuo account " + "http://172.31.6.2:8080/utente/verifica/";
    }

    public String Body_password_recovery() {
    return this.body = "Accedere al seguente link per il recupero della password " + "http://172.31.6.2:4200/login/recovery/";
    }

}
