package app.notification;

import app.model.Notification;
import app.repository.NotificationRepository;
import app.service.NotificationService;
import app.web.dto.SendEmailRequest;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.convert.ConversionService;
import org.springframework.mail.javamail.JavaMailSender;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith({MockitoExtension.class})
public class NotificationServiceUTest {
    @Mock
    private NotificationRepository notificationRepository;
    @Mock
    private ConversionService conversionService;
    @Mock
    private MimeMessage mimeMessage;
    @Mock
    private JavaMailSender mailSender;
    private final String URL_WEBSITE = "https://music-flow-b95e5db86662.herokuapp.com/";
    @InjectMocks
    private NotificationService notificationService;


    @Test
    void save_ShouldConvertAndSaveNotification() {
        // Arrange
        SendEmailRequest request = new SendEmailRequest();
        Notification expectedNotification = new Notification();
        when(conversionService.convert(request, Notification.class)).thenReturn(expectedNotification);

        // Act
        notificationService.save(request);

        // Assert
        verify(conversionService).convert(request, Notification.class);
        verify(notificationRepository).save(expectedNotification);
    }

    @Test
    void sendEmail_ShouldSendEmailAndSaveNotification() throws MessagingException {
        // Arrange
        SendEmailRequest request = new SendEmailRequest();
        request.setReceiver("test@example.com");
        request.setSubject("Test Subject");
        request.setDescription("Test Content");
        request.setSenderUsername("sender");

        Notification notification = new Notification();
        when(conversionService.convert(request, Notification.class)).thenReturn(notification);
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

        // Act
        notificationService.sendEmail(request);

        // Assert
        verify(conversionService).convert(request, Notification.class);
        verify(notificationRepository).save(notification);
        verify(mailSender).send(mimeMessage);
    }

    @Test
    void getAll_ShouldReturnNotifications() {
        boolean forAdmin = true;
        List<Notification> expectedNotifications = List.of(new Notification());
        when(notificationRepository.findAllByForAdmin(forAdmin)).thenReturn(expectedNotifications);

        List<Notification> result = notificationService.getAll(forAdmin);

        assertEquals(expectedNotifications, result);
    }

    @Test
    void getAllRead_ShouldReturnReadNotifications() {
        boolean isRead = true;
        boolean forAdmin = false;
        List<Notification> expectedNotifications = List.of(new Notification());
        when(notificationRepository.findAllByReadNotificationAndForAdmin(isRead, forAdmin))
                .thenReturn(expectedNotifications);

        List<Notification> result = notificationService.getAllRead(isRead, forAdmin);

        assertEquals(expectedNotifications, result);
    }

    @Test
    void makeNotificationRead_ShouldUpdateNotification() {
        UUID id = UUID.randomUUID();
        Notification notification = new Notification();
        notification.setReadNotification(false);
        when(notificationRepository.findById(id)).thenReturn(java.util.Optional.of(notification));

        // Act
        notificationService.makeNotificationRead(id);

        assertTrue(notification.isReadNotification());
        verify(notificationRepository).save(notification);
    }

    @Test
    void getById_WhenNotificationExists_ShouldReturnNotification() {
        UUID id = UUID.randomUUID();
        Notification expectedNotification = new Notification();
        when(notificationRepository.findById(id)).thenReturn(java.util.Optional.of(expectedNotification));

        Notification result = notificationService.getById(id);

        assertEquals(expectedNotification, result);
    }

    @Test
    void getById_WhenNotificationNotExists_ShouldThrowException() {
        UUID id = UUID.randomUUID();
        when(notificationRepository.findById(id)).thenReturn(java.util.Optional.empty());

        assertThrows(RuntimeException.class, () -> notificationService.getById(id));
    }

    @Test
    void deleteNotificationById_ShouldDeleteNotification() {
        UUID id = UUID.randomUUID();
        Notification notification = new Notification();
        when(notificationRepository.findById(id)).thenReturn(java.util.Optional.of(notification));

        notificationService.deleteNotificationById(id);

        verify(notificationRepository).delete(notification);
    }
}

