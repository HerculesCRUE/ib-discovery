package es.um.asio.service.service;

import javax.mail.MessagingException;
import java.util.List;

public interface EmailService {
    public void sendSimpleMail(List<String> to, String subject, String text) throws MessagingException;
}
