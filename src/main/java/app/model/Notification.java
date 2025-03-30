package app.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Table(name = "notifications")
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private String senderEmail;
    private String senderUsername;
    @Column(nullable = false)
    private String receiver;
    @Column(nullable = false)
    private String subject;
    @Column(nullable = false)
    private String description;
    private boolean forAdmin;
    private boolean readNotification;
//    private boolean isDeleted;
    private UUID userId;
    @CreationTimestamp
    private LocalDateTime createdAt;
    @UpdateTimestamp
    private LocalDateTime updatedAt;
    @Enumerated(EnumType.STRING)
    private MessageType messageType;

}
