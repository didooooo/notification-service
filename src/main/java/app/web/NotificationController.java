package app.web;

import app.model.Notification;
import app.service.NotificationService;
import app.web.dto.SendEmailRequest;
import jakarta.mail.MessagingException;
import org.aspectj.weaver.ast.Not;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/notifications")
public class NotificationController {
    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @PostMapping
    public ResponseEntity<Void> sendNotification(@RequestBody SendEmailRequest request) {
        notificationService.save(request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/email")
    public ResponseEntity<Void> sendEmail(@RequestBody SendEmailRequest request) throws MessagingException {
        notificationService.sendEmail(request);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<List<Notification>> getAllNotifications() {
        List<Notification> notifications = notificationService.getAll(true);
        return ResponseEntity.ok().body(notifications);
    }

    @GetMapping("/read")
    public ResponseEntity<List<Notification>> getReadNotifications() {
        List<Notification> notifications = notificationService.getAllRead(true, true);
        return ResponseEntity.ok().body(notifications);
    }

    @GetMapping("/unread")
    public ResponseEntity<List<Notification>> getUnreadNotifications() {
        List<Notification> notifications = notificationService.getAllRead(false, true);
        return ResponseEntity.ok().body(notifications);
    }

    @GetMapping("/emails")
    public ResponseEntity<List<Notification>> getSendEmails() {
        List<Notification> notifications = notificationService.getAll(false);
        return ResponseEntity.ok().body(notifications);
    }
    @PutMapping("/{id}")
    public ResponseEntity<Void> updateNotification(@PathVariable UUID id) {
        notificationService.makeNotificationRead(id);
        return ResponseEntity.ok().build();
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNotification(@PathVariable UUID id) {
        notificationService.deleteNotificationById(id);
        return ResponseEntity.ok().build();
    }
}
