package app.web;

import app.model.Notification;
import app.service.NotificationService;
import app.web.dto.SendEmailRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.mail.MessagingException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(NotificationController.class)
public class NotificationControllerApiTest {

    @MockitoBean
    private NotificationService notificationService;
    @Autowired
    private MockMvc mockMvc;

    @Test
    void sendNotification_shouldReturnOk() throws Exception {
        // Arrange
        SendEmailRequest request = new SendEmailRequest();
        request.setReceiver("test@example.com");
        request.setSubject("Test Subject");

        mockMvc.perform(post("/notifications")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(request)))
                .andExpect(status().isOk());
        verify(notificationService).save(any(SendEmailRequest.class));
    }

    @Test
    void sendEmail_shouldReturnOk() throws Exception {
        // Arrange
        SendEmailRequest request = new SendEmailRequest();
        request.setReceiver("test@example.com");
        request.setSubject("Test Subject");

        // Act & Assert
        mockMvc.perform(post("/notifications/email")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(request)))
                .andExpect(status().isOk());

        verify(notificationService).sendEmail(any(SendEmailRequest.class));
    }

    @Test
    void getAllNotifications_shouldReturnNotifications() throws Exception {
        // Arrange
        List<Notification> notifications = List.of(
                new Notification(),
                new Notification()
        );
        when(notificationService.getAll(true)).thenReturn(notifications);

        // Act & Assert
        mockMvc.perform(get("/notifications"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    void getReadNotifications_shouldReturnReadNotifications() throws Exception {
        // Arrange
        List<Notification> notifications = List.of(new Notification());
        when(notificationService.getAllRead(true, true)).thenReturn(notifications);

        // Act & Assert
        mockMvc.perform(get("/notifications/read"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    void getUnreadNotifications_shouldReturnUnreadNotifications() throws Exception {
        // Arrange
        List<Notification> notifications = List.of(new Notification());
        when(notificationService.getAllRead(false, true)).thenReturn(notifications);

        // Act & Assert
        mockMvc.perform(get("/notifications/unread"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    void getSendEmails_shouldReturnEmails() throws Exception {
        // Arrange
        List<Notification> notifications = List.of(new Notification());
        when(notificationService.getAll(false)).thenReturn(notifications);

        // Act & Assert
        mockMvc.perform(get("/notifications/emails"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    void updateNotification_shouldReturnOk() throws Exception {
        // Arrange
        UUID id = UUID.randomUUID();

        // Act & Assert
        mockMvc.perform(put("/notifications/{id}", id))
                .andExpect(status().isOk());

        verify(notificationService).makeNotificationRead(eq(id));
    }

    @Test
    void deleteNotification_shouldReturnOk() throws Exception {
        // Arrange
        UUID id = UUID.randomUUID();

        // Act & Assert
        mockMvc.perform(delete("/notifications/{id}", id))
                .andExpect(status().isOk());

        verify(notificationService).deleteNotificationById(eq(id));
    }

}
