package it.BasicServices.Pasquale.Mail;


/**
 * Interfaccia che rappresenta un oggetto configurato e
 * pronto all`uso per inviare un messaggio
 */
public interface SenderEmail {
    public void sendEmail(String to, String oggetto, String message);
}
