package com.example.blogpost.modules.user.service;

import org.springframework.stereotype.Service;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

@Service
public class EmailService {
    public boolean sendEmail(String message, String subject, String to) {
        Properties properties = System.getProperties();
        properties.put("mail.smtp.host", "smtp.gmail.com");
        properties.put("mail.smtp.port", "465");
        properties.put("mail.smtp.ssl.enable", "true");
        properties.put("mail.smtp.auth", "true");

        //step1: get the Session object
        Session session = Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                //String noteOneEmail = provide your email here -> be sure that you have activated 2steps verification your email so that u can send the message.
                //String noteTwoPassword = after activating the 2steps verification, copy the password that it provides.
                //return new PasswordAuthentication("noteOneEmail", "noteTwoPassword");
                return new PasswordAuthentication("learn.mike.helloworld@gmail.com", "tugoutumkwlgxrpl");
            }
        });
        session.setDebug(true);


        //step2: compose the message
        MimeMessage mimeMessage = new MimeMessage(session);
        try {
            mimeMessage.setFrom("learn.mike.helloworld@gmail.com");
            //mimeMessage.setFrom("noteOneEmail");
            mimeMessage.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
            mimeMessage.setSubject(subject);
            mimeMessage.setContent(message, "text/html");

            //step3: send the message
            Transport.send(mimeMessage);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
        return true;
    }
}
