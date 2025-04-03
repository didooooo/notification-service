package app;

import app.model.Notification;
import app.repository.NotificationRepository;
import app.service.NotificationService;
import app.web.dto.SendEmailRequest;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.convert.ConversionService;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class NotificationServiceIT {

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private ConversionService conversionService;

    @MockitoBean
    private JavaMailSender mailSender;

    @Test
    void save_shouldPersistNotification() {
        // Arrange
        SendEmailRequest request = new SendEmailRequest();
        request.setReceiver("test@example.com");
        request.setSubject("Test Subject");
        request.setDescription("Test Content");
        request.setSenderUsername("sender");

        // Act
        notificationService.save(request);

        // Assert
        List<Notification> notifications = notificationRepository.findAll();
        assertEquals(1, notifications.size());
        Notification saved = notifications.get(0);
        assertEquals("test@example.com", saved.getReceiver());
        assertEquals("Test Subject", saved.getSubject());
    }

    @Test
    void sendEmail_shouldSaveAndSendEmail() throws MessagingException {
        // Arrange
        SendEmailRequest request = new SendEmailRequest();
        request.setReceiver("test@example.com");
        request.setSubject("Test Subject");
        request.setDescription("Test Content");
        request.setSenderUsername("sender");

        MimeMessage mimeMessage = mock(MimeMessage.class);
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

        // Act
        notificationService.sendEmail(request);

        // Assert
        // Verify database
        List<Notification> notifications = notificationRepository.findAll();
        assertEquals(1, notifications.size());
        Notification saved = notifications.get(0);
        assertTrue(saved.isReadNotification());

        // Verify email sending
        verify(mailSender).send(mimeMessage);
    }

    @Test
    void sendEmail_whenEmailFails_shouldStillSaveNotification() throws MessagingException {
        // Arrange
        SendEmailRequest request = new SendEmailRequest();
        request.setReceiver("test@example.com");
        request.setSubject("Test Subject");
        request.setDescription("Test Content");
        request.setSenderUsername("sender");

        MimeMessage mimeMessage = mock(MimeMessage.class);
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        doThrow(new MailSendException("Failed to send")).when(mailSender).send(mimeMessage);

        // Act & Assert
        assertThrows(MailSendException.class, () -> notificationService.sendEmail(request));

        // Verify notification was still saved
        List<Notification> notifications = notificationRepository.findAll();
        assertEquals(1, notifications.size());
    }

    @Test
    void conversionService_shouldCorrectlyConvertRequestToNotification() {
        // Arrange
        SendEmailRequest request = new SendEmailRequest();
        request.setReceiver("test@example.com");
        request.setSubject("Test Subject");
        request.setDescription("Test Content");
        request.setSenderUsername("sender");

        // Act
        Notification notification = conversionService.convert(request, Notification.class);

        // Assert
        assertNotNull(notification);
        assertEquals("test@example.com", notification.getReceiver());
        assertEquals("Test Subject", notification.getSubject());
        assertEquals("Test Content", notification.getDescription());
    }
}
