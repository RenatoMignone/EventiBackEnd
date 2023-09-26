package it.BasicServices.Pasquale.Mail;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
class MailRunner implements CommandLineRunner {

    SenderEmail mail;

     MailRunner( @Autowired GmailEmailSender mail) {
        this.mail = mail;
    }

    @Override
    public void run(String... args) throws Exception {
        //    mail.sendEmail("pasqualem93@hotmail.com", "Mex Prova.Blocco oggetto", "Blocco corpo mex. Sono Eventi. Mex di prova. ! per pasq. mex num ");
       //     mail.sendEmail("liparulo.elisa20@gmail.com", "Mex Prova", "Sono Eventi. Mex di prova.  per elisa. mex num " + i);
       //     mail.sendEmail("mattia.marino01@gmail.com", "Mex Prova", "Sono Eventi. Mex di prova. per mattiamex. num " + i);
       //     mail.sendEmail("renato.mignone@gmail.com", "Mex Prova", "Sono Eventi. Mex di prova. per renato. mex num " + i);
        //    mail.sendEmail("angelo.saginario02@gmail.com", "Mex Prova", "Sono Eventi. Mex di prova. per angelo. mex num " + i);
        //    mail.sendEmail("Francescocostantini26@gmail.com", "Mex Prova", "Sono Eventi. Mex di prova. per francesco. mex num " + i);

    }
}
