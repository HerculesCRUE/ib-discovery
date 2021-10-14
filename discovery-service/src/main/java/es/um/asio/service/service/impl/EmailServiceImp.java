package es.um.asio.service.service.impl;

import es.um.asio.service.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.List;

@Service
public class EmailServiceImp implements EmailService {

    @Autowired
    private JavaMailSender javaMailSender;

    @Override
    public void sendSimpleMail(List<String> tos, String subject, String text) throws MessagingException {
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper;
        helper = new MimeMessageHelper(message, true);
        helper.setTo(String.join(";",tos));
        helper.setSubject(subject);
        helper.setText(text,true);
        javaMailSender.send(message);
/*        for (String to : tos) {
            msg.setTo(to);
        }

        msg.setSubject(subject);
        msg.setText(text);
        javaMailSender.send(msg);*/
    }
}
