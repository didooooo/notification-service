package app.web.converter;

import app.model.Notification;
import app.web.dto.SendEmailRequest;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class SendEmailRequestToNotification implements Converter<SendEmailRequest, Notification> {
    @Override
    public Notification convert(SendEmailRequest source) {
        return Notification.builder()
                .description(source.getDescription())
                .readNotification(false)
                .senderEmail(source.getSenderEmail())
                .userId(source.getUserId())
                .messageType(source.getMessageType())
                .senderUsername(source.getSenderUsername())
                .receiver(source.getReceiver())
                .subject(source.getSubject())
                .forAdmin(source.isForAdmin())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }
}
