package server;

//reference used :https://javaee.github.io/javamail/docs/api/com/sun/mail/smtp/package-summary.html 
//https://netcorecloud.com/tutorials/send-email-in-java-using-gmail-smtp/

import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class Email {
	
	public static final String sender =  System.getenv("SENDER_EMAIL");
	public static final String password = System.getenv("SENDER_PASSWORD");

	public static void sendEmail(String receiver, String subject, String body) {

        // Send email from through gmail smtp
        String host = "smtp.gmail.com";

        // Get system properties
        Properties properties = System.getProperties();

        // Mail server setup
        properties.put("mail.smtp.host", host);
        properties.put("mail.smtp.port", "465");
        properties.put("mail.smtp.ssl.enable", "true");
        properties.put("mail.smtp.auth", "true");

        // Get the Session object and define the sender and password information
        Session session = Session.getInstance(properties, new javax.mail.Authenticator() {

            protected PasswordAuthentication getPasswordAuthentication() {

                return new PasswordAuthentication(sender, password);

            }

        });

        session.setDebug(true);

        try {
            MimeMessage message = new MimeMessage(session);
            
            //From: header field of the header.
            message.setFrom(new InternetAddress(sender));

            //To: header field of the header.
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(receiver));

            //Subject line
            message.setSubject(subject);

            //Body of the message
            message.setText(body);

            System.out.println("sending...");
            
            //Send the message
            Transport.send(message);
            System.out.println("Sent message successfully....");
        } catch (MessagingException mex) {
            mex.printStackTrace();
        }

    }

}
