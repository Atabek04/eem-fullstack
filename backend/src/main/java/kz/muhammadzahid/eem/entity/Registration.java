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
@ToString(callSuper = true, exclude = {"event", "user", "checkedInBy"})
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
    private LocalDateTime registrationDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RegistrationStatus status;

    private boolean checkedIn = false;
    private LocalDateTime checkInTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "checked_in_by")
    private User checkedInBy;

    @Column(unique = true, length = 36)
    private String registrationCode;

    @Column(length = 500)
    private String comments;

    public enum RegistrationStatus {
        PENDING, CONFIRMED, CANCELLED, WAITLISTED
    }

    @PrePersist
    protected void onCreate() {
        if (registrationDate == null) {
            registrationDate = LocalDateTime.now();
        }
        if (registrationCode == null) {
            registrationCode = UUID.randomUUID().toString();
        }
        if (status == null) {
            status = RegistrationStatus.PENDING;
        }
    }

    public void checkIn(User checkedInBy) {
        this.checkedIn = true;
        this.checkInTime = LocalDateTime.now();
        this.checkedInBy = checkedInBy;
    }
}