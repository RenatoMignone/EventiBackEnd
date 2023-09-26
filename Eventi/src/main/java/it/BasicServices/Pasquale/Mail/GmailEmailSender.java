package it.BasicServices.Pasquale.Mail;

import com.google.zxing.WriterException;
import it.BasicServices.Mattia.qrcode.GenerateQRCode;
import it.Entities.Biglietto.BigliettoEntity;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.Getter;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Permette di inviare un email attraverso il client di Gmail.
 *
 * @author pasquale
 * @version 1
 *
 */
@Log
@Component
@Import({GmailConfigMail.class})
 public class GmailEmailSender implements SenderEmail {


   /*
     importa dal configuratore l`username. Cambiarlo
     non modifichera il mittente lo stesso da Gmail
     */
    @Getter
    @Value("progingsofteventi@gmail.com")
   // @Value("noreply.prova@gmail.com") // gmail lo cambia lo steso
    private String from;




    private final JavaMailSenderImpl emailSender;


    /**
     * @param emailSender iniettato da Spring
     *
     */
    public GmailEmailSender(@Autowired JavaMailSenderImpl emailSender) {
        this.emailSender = emailSender;

    }


    /**
     * invia l`email
     * @param to Destinatario del messaggio
     * @param oggetto   Oggetto dell`email
     * @param BodyMessage Corpo del messaggio. Al momento non e` supportato html.
     * @return
     */
    @Override
    public void sendEmail(String to, String oggetto, String BodyMessage) {
            try {
                MimeMessage message = emailSender.createMimeMessage();
                MimeMessageHelper helper = new MimeMessageHelper(message, true);
                helper.setFrom(from);
                helper.setTo(to);
                helper.setSubject(oggetto);
                helper.setText(BodyMessage);

                emailSender.send(message);
                log.info("INVIATA EMAIL A " + to);


            } catch (MessagingException e) {
                throw new RuntimeException(e);
            }
    }

    public void sendEmailQR(String to, String oggetto, String BodyMessage, BigliettoEntity biglietto) {
        try {
            MimeMessage message = emailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom(from);
            helper.setTo(to);
            helper.setSubject(oggetto);
            helper.setText(BodyMessage);

            byte[] qrCodeImage = GenerateQRCode.generate(biglietto.toString());
            helper.addAttachment("QRCode.png", new ByteArrayResource(qrCodeImage, "image/png"));

            emailSender.send(message);
            log.info("INVIATA EMAIL A " + to);

        } catch (MessagingException | WriterException | IOException e) {
            throw new RuntimeException(e);
        }


    }

    public void invioEmailProva(){
        log.info("EMAIL inviata");
    }
}
