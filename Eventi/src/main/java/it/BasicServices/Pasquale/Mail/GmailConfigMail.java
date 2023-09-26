package it.BasicServices.Pasquale.Mail;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

@Configuration
public class GmailConfigMail {

    @Autowired private Environment env;

    @Bean
    public JavaMailSenderImpl mail(){
        JavaMailSenderImpl imp = new JavaMailSenderImpl();
                imp.setHost("smtp.gmail.com");
        imp.setPort(587);
        imp.setUsername("progingsofteventi@gmail.com");
        imp.setPassword("pxhwtivzuzkvtnfn");
        Properties prop =imp.getJavaMailProperties();
        prop.put("mail.transport.protocol", "smtp");
        prop.put("mail.smtp.auth", "true");
        prop.put("mail.smtp.starttls.enable", "true");
        prop.put("mail.debug", "true");
prop.put("mail.smtp.starttls.require","true");
        return imp;
    }
}
