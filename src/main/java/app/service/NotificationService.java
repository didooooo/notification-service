package app.service;

import app.model.Notification;
import app.repository.NotificationRepository;
import app.web.dto.SendEmailRequest;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.core.convert.ConversionService;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class NotificationService {
    private final NotificationRepository notificationRepository;
    private final ConversionService conversionService;
    private final JavaMailSender mailSender;
    private final String URL_WEBSITE = "https://music-flow-b95e5db86662.herokuapp.com/";

    public NotificationService(NotificationRepository notificationRepository, ConversionService conversionService, JavaMailSender mailSender) {
        this.notificationRepository = notificationRepository;
        this.conversionService = conversionService;
        this.mailSender = mailSender;
    }

    public void save(SendEmailRequest request) {
        Notification notification = conversionService.convert(request, Notification.class);
        notificationRepository.save(notification);
    }

    public void sendEmail(SendEmailRequest request) throws MessagingException {
        Notification notification = conversionService.convert(request, Notification.class);
        notification.setReadNotification(true);
        notificationRepository.save(notification);
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        String emailBody = buildEmailBody(request.getSenderUsername(), request.getDescription());
        helper.setTo(request.getReceiver());
        helper.setSubject(request.getSubject());
        helper.setText(emailBody, true);
        mailSender.send(message);
    }
    private String buildEmailBody(String username, String messageContent) {
        return String.format("""
            <html>
            <head>
                <style>
                    body {
                        font-family: Arial, sans-serif;
                        background-color: #f4f4f4;
                        padding: 20px;
                    }
                    .container {
                        background: #fff;
                        padding: 20px;
                        border-radius: 8px;
                        box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
                        max-width: 600px;
                        margin: auto;
                    }
                    .header {
                        background: #106060;
                        padding: 15px;
                        color: #fff;
                        text-align: center;
                        font-size: 20px;
                        font-weight: bold;
                        border-radius: 8px 8px 0 0;
                    }
                    .content {
                        padding: 20px;
                        font-size: 16px;
                        color: #333;
                    }
                    .footer {
                        text-align: center;
                        padding: 10px;
                        font-size: 14px;
                        color: #555;
                    }
                    .button {
                        display: inline-block;
                        padding: 10px 20px;
                        margin-top: 10px;
                        font-size: 16px;
                        color: white;
                        background-color: #106060;
                        text-decoration: none;
                        border-radius: 5px;
                    }
                    .button:hover {
                        background-color: #0d4d4d;
                    }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">Hello, %s!</div>
                    <div class="content">
                        <p>%s</p>
                        <p>Thank you for being with us!</p>
                        <a href="%s" class="button">Visit Our Website</a>
                    </div>
                    <div class="footer">
                        &copy; 2025 Music Flow. All rights reserved.
                    </div>
                </div>
            </body>
            </html>
            """, username, messageContent,URL_WEBSITE);
    }

    public List<Notification> getAll(boolean forAdmin) {
        return notificationRepository.findAllByForAdmin(forAdmin);
    }

    public List<Notification> getAllRead(boolean isRead,boolean forAdmin) {
        return notificationRepository.findAllByReadNotificationAndForAdmin(isRead,forAdmin);
    }

    public void makeNotificationRead(UUID id) {
        Notification notification = getById(id);
        notification.setReadNotification(true);
        notificationRepository.save(notification);
    }

    public Notification getById(UUID id) {
        return notificationRepository.findById(id).orElseThrow(()->new RuntimeException("Could not find notification with id: " + id));
    }

    public void deleteNotificationById(UUID id) {
        Notification notification = getById(id);
        notificationRepository.delete(notification);
    }
}
