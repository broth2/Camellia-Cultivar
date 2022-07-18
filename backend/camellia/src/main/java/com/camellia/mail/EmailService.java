package com.camellia.mail;

import java.util.Date;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.PasswordAuthentication;

import org.springframework.stereotype.Service;

@Service
public class EmailService {
    private Properties properties;
    private Session session;

    public EmailService() {
        properties = System.getProperties();

        properties.put("mail.smtp.user", EmailConsts.OUR_EMAIL );
        properties.put("mail.smtp.host", "smtp.gmail.com");
        properties.put("mail.smtp.port", "587");
        properties.put("mail.smtp.starttls.enable","true");
        properties.put("mail.smtp.debug", "true");
        properties.put("mail.smtp.auth", "true");
        //properties.put("mail.smtp.socketFactory.port", "587");
        //properties.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        //properties.put("mail.smtp.socketFactory.fallback", "false");


        session = Session.getInstance(properties, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(EmailConsts.OUR_EMAIL, EmailConsts.PSW_EMAIL);
            }
        });
    }

    public boolean send(Email e) {
        try {
            MimeMessage message = new MimeMessage(this.session);
            message.setFrom(new InternetAddress(e.getFrom()));
            message.addRecipients(Message.RecipientType.TO, InternetAddress.parse(e.getTo()));
            message.setSubject(e.getSubject());
            message.setContent(e.getText(), "text/html; charset=utf-8");
            message.setSentDate(new Date());

            
            Transport tr = session.getTransport("smtp");
            tr.connect("smtp.gmail.com", 587, EmailConsts.OUR_EMAIL, EmailConsts.PSW_EMAIL);
            tr.sendMessage(message, message.getAllRecipients());
            tr.close();
            return true;
        } catch (MessagingException mex) {
            mex.printStackTrace();
            return false;
        }
    }
}

