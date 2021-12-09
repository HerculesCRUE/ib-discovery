package es.um.asio.service.service.impl;

import java.util.List;

import javax.mail.MessagingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import es.um.asio.service.service.EmailService;

@Service
public class EmailServiceImp implements EmailService {

	@Autowired
	private JavaMailSender javaMailSender;

	@Value("${spring.mail.username}")
	private String from;

	private final Logger logger = LoggerFactory.getLogger(EmailServiceImp.class);

	@Override
	public void sendSimpleMail(List<String> tos, String subject, String text) throws MessagingException {
		try {
			SimpleMailMessage msg = new SimpleMailMessage();
			for (String to : tos) {
				msg.setTo(to);
			}
			msg.setFrom(from);
			msg.setSubject(subject);
			msg.setText(text);
			javaMailSender.send(msg);
			logger.info("Send Mail to: {}, subject: {}, text: {}", String.join(";", tos), subject, text);
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		/*
		 * for (String to : tos) { msg.setTo(to); }
		 * 
		 * msg.setSubject(subject); msg.setText(text); javaMailSender.send(msg);
		 */
	}
}
