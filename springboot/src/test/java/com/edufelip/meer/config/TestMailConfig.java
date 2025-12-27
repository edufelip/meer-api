package com.edufelip.meer.config;

import jakarta.mail.MessagingException;
import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;
import java.io.InputStream;
import java.util.Properties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.mail.MailParseException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessagePreparator;

@Configuration
@Profile("test")
public class TestMailConfig {

  @Bean
  @Primary
  public JavaMailSender javaMailSender() {
    return new JavaMailSender() {
      private final Session session = Session.getInstance(new Properties());

      @Override
      public MimeMessage createMimeMessage() {
        return new MimeMessage(session);
      }

      @Override
      public MimeMessage createMimeMessage(InputStream contentStream) {
        try {
          return new MimeMessage(session, contentStream);
        } catch (MessagingException ex) {
          throw new MailParseException(ex);
        }
      }

      @Override
      public void send(MimeMessage mimeMessage) {}

      @Override
      public void send(MimeMessage... mimeMessages) {}

      @Override
      public void send(MimeMessagePreparator mimeMessagePreparator) {}

      @Override
      public void send(MimeMessagePreparator... mimeMessagePreparators) {}

      @Override
      public void send(SimpleMailMessage simpleMessage) {}

      @Override
      public void send(SimpleMailMessage... simpleMessages) {}
    };
  }
}
