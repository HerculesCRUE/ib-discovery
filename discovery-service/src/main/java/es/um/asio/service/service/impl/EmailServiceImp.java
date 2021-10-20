package es.um.asio.service.service.impl;

import es.um.asio.service.service.EmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private final Logger logger = LoggerFactory.getLogger(EmailServiceImp.class);

    @Override
    public void sendSimpleMail(List<String> tos, String subject, String text) throws MessagingException {
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper;
            helper = new MimeMessageHelper(message, true);
            helper.setTo(String.join(";", tos));
            helper.setSubject(subject);
            helper.setText(text, true);
            javaMailSender.send(message);
            logger.info("Send Mail to: {}, subject: {}, text: {}", String.join(";", tos), subject, text);
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
/*        for (String to : tos) {
            msg.setTo(to);
        }

        msg.setSubject(subject);
        msg.setText(text);
        javaMailSender.send(msg);*/
    }
}
