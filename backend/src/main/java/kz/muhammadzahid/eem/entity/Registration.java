package kz.muhammadzahid.eem.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "registrations", indexes = {
        @Index(name = "idx_reg_event", columnList = "event_id"),
        @Index(name = "idx_reg_user", columnList = "user_id"),
        @Index(name = "idx_reg_code", columnList = "registrationCode")
})
@ToString(callSuper = true, exclude = {"event", "user"})
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
public class Registration extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private LocalDateTime registrationTime;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RegistrationStatus status;

    @Column(unique = true, length = 36)
    private String registrationCode;

    @Column(length = 500)
    private String cancelReason;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column
    private LocalDateTime updatedAt;

    @Column
    private String comments;

    // Use version for optimistic locking to prevent race conditions
    @Version
    private Integer version;

    public enum RegistrationStatus {
        CONFIRMED, CANCELLED
    }

    @PrePersist
    protected void onCreate() {
        if (registrationTime == null) {
            registrationTime = LocalDateTime.now();
        }
        if (registrationCode == null) {
            registrationCode = UUID.randomUUID().toString();
        }
        if (status == null) {
            status = RegistrationStatus.CONFIRMED;
        }
        createdAt = LocalDateTime.now();
    }

    /**
     * Cancel this registration
     * @param reason Optional reason for cancellation
     */
    public void cancel(String reason) {
        this.status = RegistrationStatus.CANCELLED;
        this.cancelReason = reason;
        this.updatedAt = LocalDateTime.now();
        
        // Update event capacity counter
        if (event != null) {
            event.unregisterAttendee();
        }
    }
}