package com.bix.processor.application;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

//@Configuration
//public class EmailConfig {
//    @Bean
//    public JavaMailSender javaMailSender() {
//        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
//
//        Properties props = mailSender.getJavaMailProperties();
//        props.put("mail.transport.protocol", "smtp");
//        props.put("mail.smtp.auth", "false");
//        props.put("mail.smtp.starttls.enable", "false");
//        props.put("mail.debug", "true");
//
//        // Explicitly disable SSL
//        props.put("mail.smtp.ssl.trust", "*");
//
//        return mailSender;
//    }
//}

@Configuration
@Profile("dev")
@Slf4j
public class MockMailConfig {
    @Bean
    public JavaMailSender javaMailSender() {
        return new JavaMailSenderImpl() {
            @Override
            public void send(org.springframework.mail.SimpleMailMessage simpleMessage) {
                // Log the email instead of sending it
                log.info("Mocking email send: To={}, Subject={}, Text={}",
                        simpleMessage.getTo(), simpleMessage.getSubject(), simpleMessage.getText());
            }

            @Override
            public void send(org.springframework.mail.SimpleMailMessage... simpleMessages) {
                for (SimpleMailMessage message : simpleMessages) {
                    send(message);
                }
            }
        };
    }
}