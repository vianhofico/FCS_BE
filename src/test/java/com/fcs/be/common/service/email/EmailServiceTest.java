package com.fcs.be.common.service.email;

import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Properties;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EmailServiceTest {

    @Mock
    private JavaMailSender javaMailSender;

    @Test
    void sendPasswordResetEmailSendsHtmlResetMessage() throws Exception {
        MimeMessage message = new MimeMessage(Session.getInstance(new Properties()));
        when(javaMailSender.createMimeMessage()).thenReturn(message);
        EmailService emailService = new EmailService(javaMailSender);

        emailService.sendPasswordResetEmail("buyer@example.com", "http://localhost:5173/reset-password?token=abc");

        ArgumentCaptor<MimeMessage> messageCaptor = ArgumentCaptor.forClass(MimeMessage.class);
        verify(javaMailSender).send(messageCaptor.capture());
        MimeMessage sentMessage = messageCaptor.getValue();
        assertEquals("Reset Your Password - Fashion Consignment System", sentMessage.getSubject());
        assertEquals("buyer@example.com", sentMessage.getAllRecipients()[0].toString());
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        sentMessage.writeTo(output);
        String rawMessage = output.toString(StandardCharsets.UTF_8);
        assertTrue(rawMessage.contains("http://localhost:5173/reset-password?token=abc"));
    }
}
