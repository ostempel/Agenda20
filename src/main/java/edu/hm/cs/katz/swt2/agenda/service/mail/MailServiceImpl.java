package edu.hm.cs.katz.swt2.agenda.service.mail;

import java.util.Properties;
import org.springframework.context.annotation.Bean;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Component;

@Component
public class MailServiceImpl {

  @Bean
  public static  JavaMailSender getJavaMailSender() {
    JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
    mailSender.setHost("smtp.gmail.com");
    mailSender.setPort(587);

    mailSender.setUsername("sosagenda20@gmail.com");
    mailSender.setPassword("agenda20.?");

    Properties props = mailSender.getJavaMailProperties();
    props.put("mail.transport.protocol", "smtp");
    props.put("mail.smtp.auth", "true");
    props.put("mail.smtp.starttls.enable", "true");
    props.put("mail.debug", "true");

    return mailSender;
  }
  
  public static void sendMail(String to, String subject, String text) {
    SimpleMailMessage message = new SimpleMailMessage();

    message.setTo(to);
    message.setSubject(subject);
    message.setText(text);

    getJavaMailSender().send(message);
  }
}
