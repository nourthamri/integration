package services;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

public class EmailC {

    private static final String SMTP_HOST = "smtp.ethereal.email";
    private static final String SMTP_PORT = "587";
    private static final String EMAIL = "daryl.kuhn@ethereal.email";
    private static final String PASSWORD = "rqqEd1XcB7KRcfYSt2";

    public void envoyerEmail(String destinataire, String sujet, String contenu) throws MessagingException {
        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.ethereal.email");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        Session session = Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(EMAIL, PASSWORD);
            }
        });

        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(EMAIL));
        message.setRecipient(Message.RecipientType.TO, new InternetAddress(destinataire));
        message.setSubject(sujet);
        message.setText(contenu);
        Transport.send(message);
    }
}

